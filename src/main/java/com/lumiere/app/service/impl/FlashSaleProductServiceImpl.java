package com.lumiere.app.service.impl;

import com.lumiere.app.domain.FlashSaleProduct;
import com.lumiere.app.domain.ProductVariant;
import com.lumiere.app.domain.User;
import com.lumiere.app.repository.FlashSaleProductRepository;
import com.lumiere.app.repository.FlashSaleRepository;
import com.lumiere.app.repository.ProductVariantRepository;
import com.lumiere.app.repository.UserRepository;
import com.lumiere.app.service.FlashSaleProductService;
import com.lumiere.app.service.MailService;
import com.lumiere.app.service.dto.FlashSaleProductDTO;
import com.lumiere.app.service.mapper.FlashSaleProductMapper;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumiere.app.domain.FlashSaleProduct}.
 */
@Service
@Transactional
public class FlashSaleProductServiceImpl implements FlashSaleProductService {

    private static final Logger LOG = LoggerFactory.getLogger(FlashSaleProductServiceImpl.class);

    private final FlashSaleProductRepository flashSaleProductRepository;

    private final FlashSaleProductMapper flashSaleProductMapper;

    private final MailService mailService;

    private final UserRepository userRepository;

    private final FlashSaleRepository flashSaleRepository;

    private final ProductVariantRepository productVariantRepository;

    public FlashSaleProductServiceImpl(
        FlashSaleProductRepository flashSaleProductRepository,
        FlashSaleProductMapper flashSaleProductMapper,
        MailService mailService,
        UserRepository userRepository,
        FlashSaleRepository flashSaleRepository,
        ProductVariantRepository productVariantRepository
    ) {
        this.flashSaleProductRepository = flashSaleProductRepository;
        this.flashSaleProductMapper = flashSaleProductMapper;
        this.mailService = mailService;
        this.userRepository = userRepository;
        this.flashSaleRepository = flashSaleRepository;
        this.productVariantRepository = productVariantRepository;
    }

    @Override
    public FlashSaleProductDTO save(FlashSaleProductDTO flashSaleProductDTO) {
        LOG.debug("Request to save FlashSaleProduct : {}", flashSaleProductDTO);

        // Validation
        validateFlashSaleProduct(flashSaleProductDTO);

        FlashSaleProduct flashSaleProduct = flashSaleProductMapper.toEntity(flashSaleProductDTO);
        flashSaleProduct = flashSaleProductRepository.save(flashSaleProduct);

        // Load lại với eager relationships để có đầy đủ thông tin
        flashSaleProduct = flashSaleProductRepository
            .findOneWithEagerRelationships(flashSaleProduct.getId())
            .orElse(flashSaleProduct);

        // Gửi email thông báo flash sale sau khi tạo thành công
        sendFlashSaleNotificationEmails(flashSaleProduct);

        return flashSaleProductMapper.toDto(flashSaleProduct);
    }

    private void validateFlashSaleProduct(FlashSaleProductDTO flashSaleProductDTO) {
        if (flashSaleProductDTO.getQuantity() == null || flashSaleProductDTO.getQuantity() <= 0) {
            throw new IllegalArgumentException("Số lượng sản phẩm phải lớn hơn 0");
        }

        if (flashSaleProductDTO.getSalePrice() == null || flashSaleProductDTO.getSalePrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Giá giảm phải lớn hơn hoặc bằng 0");
        }

        if (flashSaleProductDTO.getSold() == null || flashSaleProductDTO.getSold() < 0) {
            throw new IllegalArgumentException("Số lượng đã bán phải lớn hơn hoặc bằng 0");
        }

        if (flashSaleProductDTO.getSold() > flashSaleProductDTO.getQuantity()) {
            throw new IllegalArgumentException("Số lượng đã bán không được vượt quá số lượng tổng");
        }

        // Kiểm tra flash sale và product variant tồn tại
        if (flashSaleProductDTO.getFlashSale() == null || flashSaleProductDTO.getFlashSale().getId() == null) {
            throw new IllegalArgumentException("Flash sale không được để trống");
        }

        if (flashSaleProductDTO.getProductVariant() == null || flashSaleProductDTO.getProductVariant().getId() == null) {
            throw new IllegalArgumentException("Biến thể sản phẩm không được để trống");
        }

        flashSaleRepository
            .findById(flashSaleProductDTO.getFlashSale().getId())
            .orElseThrow(() -> new IllegalArgumentException("Flash sale không tồn tại"));

        productVariantRepository
            .findById(flashSaleProductDTO.getProductVariant().getId())
            .orElseThrow(() -> new IllegalArgumentException("Biến thể sản phẩm không tồn tại"));
    }

    private void sendFlashSaleNotificationEmails(FlashSaleProduct flashSaleProduct) {
        try {
            // Lấy thông tin flash sale và product variant
            String flashSaleName = flashSaleProduct.getFlashSale() != null ? flashSaleProduct.getFlashSale().getName() : "Flash Sale";
            ProductVariant productVariant = flashSaleProduct.getProductVariant();
            String productName = productVariant != null && productVariant.getProduct() != null 
                ? productVariant.getProduct().getName() 
                : "Sản phẩm";
            BigDecimal salePrice = flashSaleProduct.getSalePrice();

            // Lấy giá gốc từ product variant
            BigDecimal originalPrice = productVariant != null && productVariant.getPrice() != null 
                ? productVariant.getPrice() 
                : BigDecimal.ZERO;

            // Tạo URL sản phẩm
            String productUrl = buildProductUrl(productVariant);

            // Lấy danh sách tất cả người dùng đã kích hoạt để gửi email
            List<User> activatedUsers = userRepository.findAllByIdNotNullAndActivatedIsTrue(Pageable.unpaged()).getContent();

            LOG.debug("Sending flash sale notification emails to {} users", activatedUsers.size());

            // Gửi email cho từng người dùng
            for (User user : activatedUsers) {
                try {
                    mailService.sendFlashSaleNotificationEmail(
                        user,
                        flashSaleName,
                        productName,
                        salePrice,
                        originalPrice,
                        productUrl
                    );
                } catch (Exception e) {
                    LOG.warn("Failed to send flash sale email to user '{}': {}", user.getEmail(), e.getMessage());
                }
            }

            LOG.debug("Flash sale notification emails sent successfully");
        } catch (Exception e) {
            LOG.error("Error sending flash sale notification emails", e);
            // Không throw exception để không ảnh hưởng đến việc tạo flash sale product
        }
    }

    private String buildProductUrl(ProductVariant productVariant) {
        if (productVariant == null || productVariant.getProduct() == null || productVariant.getProduct().getSlug() == null) {
            return "";
        }
        // Giả sử base URL được lấy từ MailService hoặc config
        // Ở đây tạm thời trả về slug, sẽ được xử lý trong email template
        return "/products/" + productVariant.getProduct().getSlug();
    }

    @Override
    public FlashSaleProductDTO update(FlashSaleProductDTO flashSaleProductDTO) {
        LOG.debug("Request to update FlashSaleProduct : {}", flashSaleProductDTO);
        validateFlashSaleProduct(flashSaleProductDTO);
        FlashSaleProduct flashSaleProduct = flashSaleProductMapper.toEntity(flashSaleProductDTO);
        flashSaleProduct = flashSaleProductRepository.save(flashSaleProduct);
        return flashSaleProductMapper.toDto(flashSaleProduct);
    }

    @Override
    public Optional<FlashSaleProductDTO> partialUpdate(FlashSaleProductDTO flashSaleProductDTO) {
        LOG.debug("Request to partially update FlashSaleProduct : {}", flashSaleProductDTO);

        return flashSaleProductRepository
            .findById(flashSaleProductDTO.getId())
            .map(existingFlashSaleProduct -> {
                flashSaleProductMapper.partialUpdate(existingFlashSaleProduct, flashSaleProductDTO);

                return existingFlashSaleProduct;
            })
            .map(flashSaleProductRepository::save)
            .map(flashSaleProductMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlashSaleProductDTO> findAll() {
        LOG.debug("Request to get all FlashSaleProducts");
        return flashSaleProductRepository
            .findAll()
            .stream()
            .map(flashSaleProductMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    public Page<FlashSaleProductDTO> findAllWithEagerRelationships(Pageable pageable) {
        return flashSaleProductRepository.findAllWithEagerRelationships(pageable).map(flashSaleProductMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FlashSaleProductDTO> findOne(Long id) {
        LOG.debug("Request to get FlashSaleProduct : {}", id);
        return flashSaleProductRepository.findOneWithEagerRelationships(id).map(flashSaleProductMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete FlashSaleProduct : {}", id);
        flashSaleProductRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlashSaleProductDTO> findByFlashSaleId(Long flashSaleId) {
        LOG.debug("Request to get FlashSaleProducts by flashSaleId : {}", flashSaleId);
        return flashSaleProductRepository
            .findByFlashSaleId(flashSaleId)
            .stream()
            .map(flashSaleProductMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FlashSaleProductDTO> findActiveByProductVariantId(Long productVariantId) {
        LOG.debug("Request to get active FlashSaleProduct by productVariantId : {}", productVariantId);
        java.time.Instant now = java.time.Instant.now();
        return flashSaleProductRepository.findActiveByProductVariantId(productVariantId, now).map(flashSaleProductMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FlashSaleProductDTO> findActiveByProductId(Long productId) {
        LOG.debug("Request to get active FlashSaleProduct by productId : {}", productId);
        java.time.Instant now = java.time.Instant.now();
        return flashSaleProductRepository.findActiveByProductId(productId, now).map(flashSaleProductMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlashSaleProductDTO> findByProductVariantId(Long productVariantId) {
        LOG.debug("Request to get FlashSaleProducts by productVariantId : {}", productVariantId);
        return flashSaleProductRepository
            .findByProductVariantId(productVariantId)
            .stream()
            .map(flashSaleProductMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlashSaleProductDTO> findByProductId(Long productId) {
        LOG.debug("Request to get FlashSaleProducts by productId : {}", productId);
        return flashSaleProductRepository
            .findByProductId(productId)
            .stream()
            .map(flashSaleProductMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlashSaleProductDTO> findAvailableProducts() {
        LOG.debug("Request to get all available FlashSaleProducts");
        return flashSaleProductRepository
            .findAvailableProducts()
            .stream()
            .map(flashSaleProductMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlashSaleProductDTO> findAllSortedByDiscountPercent() {
        LOG.debug("Request to get all FlashSaleProducts sorted by discount percentage");
        return flashSaleProductRepository
            .findAllWithToOneRelationships()
            .stream()
            .map(flashSaleProductMapper::toDto)
            .sorted((a, b) -> {
                // Tính phần trăm giảm giá cho sản phẩm a
                BigDecimal originalPriceA = a.getProductVariant() != null && a.getProductVariant().getPrice() != null
                    ? a.getProductVariant().getPrice()
                    : BigDecimal.ZERO;
                BigDecimal salePriceA = a.getSalePrice() != null ? a.getSalePrice() : BigDecimal.ZERO;
                BigDecimal discountPercentA = BigDecimal.ZERO;
                if (originalPriceA.compareTo(BigDecimal.ZERO) > 0) {
                    discountPercentA = originalPriceA.subtract(salePriceA)
                        .divide(originalPriceA, 4, java.math.RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                }

                // Tính phần trăm giảm giá cho sản phẩm b
                BigDecimal originalPriceB = b.getProductVariant() != null && b.getProductVariant().getPrice() != null
                    ? b.getProductVariant().getPrice()
                    : BigDecimal.ZERO;
                BigDecimal salePriceB = b.getSalePrice() != null ? b.getSalePrice() : BigDecimal.ZERO;
                BigDecimal discountPercentB = BigDecimal.ZERO;
                if (originalPriceB.compareTo(BigDecimal.ZERO) > 0) {
                    discountPercentB = originalPriceB.subtract(salePriceB)
                        .divide(originalPriceB, 4, java.math.RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                }

                // Sắp xếp giảm dần (giảm nhiều nhất lên trên)
                return discountPercentB.compareTo(discountPercentA);
            })
            .collect(Collectors.toList());
    }
}
