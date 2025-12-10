package com.lumiere.app.service.impl;

import com.lumiere.app.domain.FlashSaleProduct;
import com.lumiere.app.domain.Product;
import com.lumiere.app.domain.ProductVariant;
import com.lumiere.app.domain.User;
import com.lumiere.app.repository.FlashSaleProductRepository;
import com.lumiere.app.repository.FlashSaleRepository;
import com.lumiere.app.repository.ProductRepository;
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

    private final ProductRepository productRepository;

    private final ProductVariantRepository productVariantRepository;

    public FlashSaleProductServiceImpl(
        FlashSaleProductRepository flashSaleProductRepository,
        FlashSaleProductMapper flashSaleProductMapper,
        MailService mailService,
        UserRepository userRepository,
        FlashSaleRepository flashSaleRepository,
        ProductRepository productRepository,
        ProductVariantRepository productVariantRepository
    ) {
        this.flashSaleProductRepository = flashSaleProductRepository;
        this.flashSaleProductMapper = flashSaleProductMapper;
        this.mailService = mailService;
        this.userRepository = userRepository;
        this.flashSaleRepository = flashSaleRepository;
        this.productRepository = productRepository;
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

        // Kiểm tra flash sale và product tồn tại
        if (flashSaleProductDTO.getFlashSale() == null || flashSaleProductDTO.getFlashSale().getId() == null) {
            throw new IllegalArgumentException("Flash sale không được để trống");
        }

        if (flashSaleProductDTO.getProduct() == null || flashSaleProductDTO.getProduct().getId() == null) {
            throw new IllegalArgumentException("Sản phẩm không được để trống");
        }

        flashSaleRepository
            .findById(flashSaleProductDTO.getFlashSale().getId())
            .orElseThrow(() -> new IllegalArgumentException("Flash sale không tồn tại"));

        productRepository
            .findById(flashSaleProductDTO.getProduct().getId())
            .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại"));
    }

    private void sendFlashSaleNotificationEmails(FlashSaleProduct flashSaleProduct) {
        try {
            // Lấy thông tin flash sale và product
            String flashSaleName = flashSaleProduct.getFlashSale() != null ? flashSaleProduct.getFlashSale().getName() : "Flash Sale";
            String productName = flashSaleProduct.getProduct() != null ? flashSaleProduct.getProduct().getName() : "Sản phẩm";
            BigDecimal salePrice = flashSaleProduct.getSalePrice();

            // Lấy giá gốc từ product variant (lấy variant đầu tiên hoặc variant mặc định)
            BigDecimal originalPrice = getOriginalPrice(flashSaleProduct.getProduct());

            // Tạo URL sản phẩm
            String productUrl = buildProductUrl(flashSaleProduct.getProduct());

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

    private BigDecimal getOriginalPrice(Product product) {
        if (product == null) {
            return BigDecimal.ZERO;
        }

        // Lấy variant mặc định hoặc variant đầu tiên
        List<ProductVariant> variants = productVariantRepository.findByProductId(product.getId());
        if (variants.isEmpty()) {
            return BigDecimal.ZERO;
        }

        // Tìm variant mặc định
        Optional<ProductVariant> defaultVariant = variants.stream().filter(ProductVariant::getDefault).findFirst();
        if (defaultVariant.isPresent()) {
            return defaultVariant.get().getPrice();
        }

        // Nếu không có variant mặc định, lấy variant đầu tiên
        return variants.get(0).getPrice();
    }

    private String buildProductUrl(Product product) {
        if (product == null || product.getSlug() == null) {
            return "";
        }
        // Giả sử base URL được lấy từ MailService hoặc config
        // Ở đây tạm thời trả về slug, sẽ được xử lý trong email template
        return "/products/" + product.getSlug();
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
    public Optional<FlashSaleProductDTO> findActiveByProductId(Long productId) {
        LOG.debug("Request to get active FlashSaleProduct by productId : {}", productId);
        java.time.Instant now = java.time.Instant.now();
        return flashSaleProductRepository.findActiveByProductId(productId, now).map(flashSaleProductMapper::toDto);
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
}
