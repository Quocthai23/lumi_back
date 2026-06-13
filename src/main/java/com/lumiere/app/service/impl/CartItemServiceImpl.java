package com.lumiere.app.service.impl;

import com.lumiere.app.domain.CartItem;
import com.lumiere.app.domain.ProductVariant;
import com.lumiere.app.repository.CartItemRepository;
import com.lumiere.app.repository.ProductVariantRepository;
import com.lumiere.app.service.CartItemService;
import com.lumiere.app.service.FlashSaleProductService;
import com.lumiere.app.service.dto.CartItemDTO;
import com.lumiere.app.service.dto.ProductVariantDTO;
import com.lumiere.app.service.mapper.CartItemMapper;
import com.lumiere.app.service.mapper.ProductVariantMapper;
import com.lumiere.app.utils.MergeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartItemServiceImpl implements CartItemService {

    private final Logger log = LoggerFactory.getLogger(CartItemServiceImpl.class);

    private final CartItemRepository cartItemRepository;

    private final CartItemMapper cartItemMapper;
    private final ProductVariantRepository productVariantRepository;
    private final ProductVariantMapper productVariantMapper;
    private final FlashSaleProductService flashSaleProductService;

    public CartItemServiceImpl(
        CartItemRepository cartItemRepository, 
        CartItemMapper cartItemMapper, 
        ProductVariantRepository productVariantRepository, 
        ProductVariantMapper productVariantMapper,
        FlashSaleProductService flashSaleProductService
    ) {
        this.cartItemRepository = cartItemRepository;
        this.cartItemMapper = cartItemMapper;
        this.productVariantRepository = productVariantRepository;
        this.productVariantMapper = productVariantMapper;
        this.flashSaleProductService = flashSaleProductService;
    }

    @Override
    public CartItemDTO save(CartItemDTO cartItemDTO) {
        log.debug("Request to save CartItem : {}", cartItemDTO);

        // Tính totalPrice nếu chưa set hoặc muốn luôn sync
        if (cartItemDTO.getQuantity() != null && cartItemDTO.getUnitPrice() != null) {
            BigDecimal total = cartItemDTO.getUnitPrice()
                .multiply(BigDecimal.valueOf(cartItemDTO.getQuantity()));
            cartItemDTO.setTotalPrice(total);
        }

        Instant now = Instant.now();
        if (cartItemDTO.getCreatedDate() == null) {
            cartItemDTO.setCreatedDate(now);
        }
        cartItemDTO.setLastModifiedDate(now);

        CartItem cartItem = cartItemMapper.toEntity(cartItemDTO);
        cartItem = cartItemRepository.save(cartItem);
        return cartItemMapper.toDto(cartItem);
    }

    @Override
    public CartItemDTO update(CartItemDTO cartItemDTO) {
        log.debug("Request to update CartItem : {}", cartItemDTO);
        Optional<CartItemDTO> exist = this.findOne(cartItemDTO.getId());

        if (exist.isEmpty()) {
            throw new IllegalArgumentException();
        }


        cartItemDTO.setCustomerId(exist.get().getCustomerId());

        if (cartItemDTO.getQuantity() != null && cartItemDTO.getUnitPrice() != null) {
            BigDecimal total = cartItemDTO.getUnitPrice()
                .multiply(BigDecimal.valueOf(cartItemDTO.getQuantity()));
            cartItemDTO.setTotalPrice(total);
        }

        cartItemDTO.setLastModifiedDate(Instant.now());

        CartItem cartItem = cartItemMapper.toEntity(cartItemDTO);
        cartItem = cartItemRepository.save(cartItem);
        return cartItemMapper.toDto(cartItem);
    }

    @Override
    public Optional<CartItemDTO> partialUpdate(CartItemDTO cartItemDTO) {
        log.debug("Request to partially update CartItem : {}", cartItemDTO);

        return cartItemRepository
            .findById(cartItemDTO.getId())
            .map(existing -> {
                // copy từng field nếu không null
                if (cartItemDTO.getCustomerId() != null) {
                    existing.setCustomerId(cartItemDTO.getCustomerId());
                }
                if (cartItemDTO.getProductId() != null) {
                    existing.setProductId(cartItemDTO.getProductId());
                }
                if (cartItemDTO.getVariantId() != null) {
                    existing.setVariantId(cartItemDTO.getVariantId());
                }
                if (cartItemDTO.getQuantity() != null) {
                    existing.setQuantity(cartItemDTO.getQuantity());
                }
                if (cartItemDTO.getUnitPrice() != null) {
                    existing.setUnitPrice(cartItemDTO.getUnitPrice());
                }
                if (cartItemDTO.getTotalPrice() != null) {
                    existing.setTotalPrice(cartItemDTO.getTotalPrice());
                }

                existing.setLastModifiedDate(Instant.now());

                // Nếu không gửi totalPrice nhưng có quantity + unitPrice thì auto tính
                if (existing.getQuantity() != null && existing.getUnitPrice() != null) {
                    BigDecimal total = existing.getUnitPrice()
                        .multiply(BigDecimal.valueOf(existing.getQuantity()));
                    existing.setTotalPrice(total);
                }

                return existing;
            })
            .map(cartItemRepository::save)
            .map(cartItemMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CartItemDTO> findAll(Pageable pageable) {
        log.debug("Request to get all CartItems");
        return cartItemRepository.findAll(pageable).map(cartItemMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CartItemDTO> findOne(Long id) {
        log.debug("Request to get CartItem : {}", id);
        return cartItemRepository.findById(id).map(cartItemMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete CartItem : {}", id);
        cartItemRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<CartItemDTO> findAllByCustomerId(Long customerId, Pageable pageable) {
        log.debug("Request to get CartItems by customerId : {}", customerId);
        return cartItemRepository.findAllByCustomerId(customerId, pageable).map(cartItemMapper::toDto);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CartItemDTO> findAllByCustomerId(Long customerId) {
        List<CartItem> items = cartItemRepository.findAllByCustomerId(customerId);
        if (items.isEmpty()) return Collections.emptyList();

        Set<Long> variantIds = items.stream()
            .map(CartItem::getVariantId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        List<ProductVariantDTO> variants = productVariantRepository.findAllByIdIn(variantIds).stream().map(productVariantMapper::toDto).toList();
        Map<Long, ProductVariantDTO> variantById = variants.stream()
            .collect(Collectors.toMap(ProductVariantDTO::getId, v -> v));

        // Set promotionPrice từ FlashSaleProduct cho mỗi variant
        variantById.values().forEach(dto -> {
            if (dto.getId() != null) {
                flashSaleProductService.findActiveByProductVariantId(dto.getId())
                    .ifPresent(flashSaleProduct -> {
                        dto.setPromotionPrice(flashSaleProduct.getSalePrice());
                    });
            }
        });

        return items.stream()
            .map(item -> {
                CartItemDTO dto = cartItemMapper.toDto(item);
                dto.setVariant(variantById.get(item.getVariantId()));
                return dto;
            })
            .collect(Collectors.toList());
    }

    @Override
    public CartItemDTO findByCustomerIdAndVariantId(Long customerId, Long variantId){
        return cartItemMapper.toDto(cartItemRepository.findCartItemByCustomerIdAndVariantId(customerId,variantId));
    }

    @Override
    public CartItemDTO createCartItem(CartItemDTO cartItemDTO, Long userId){

        CartItemDTO exist = this.findByCustomerIdAndVariantId(cartItemDTO.getVariantId(), userId);

        if(exist != null){

            MergeUtils.Options opts = new MergeUtils.Options()
                .overwriteNulls(false)
                .replaceCollections(false);
            MergeUtils.merge(exist,cartItemDTO,opts);

            exist = this.save(exist);
        }else{
            cartItemDTO.setCustomerId(userId);
            cartItemDTO.setCreatedDate(Instant.now());
            cartItemDTO.setLastModifiedDate(Instant.now());
            cartItemDTO.setTotalPrice(cartItemDTO.getUnitPrice().multiply(BigDecimal.valueOf(cartItemDTO.getQuantity())));
            exist = this.save(cartItemDTO);
        }
        return exist;
    }

    @Override
    public CartItemDTO addToCart(Long userId, Long variantId, int qty) {
        if (qty <= 0) throw new IllegalArgumentException("qty must be > 0");

        CartItem item = cartItemRepository.findCartItemByCustomerIdAndVariantId(userId, variantId);
        long newQtyLong = (long) item.getQuantity() + qty;
        if (newQtyLong > Integer.MAX_VALUE) {
            item.setQuantity(Integer.MAX_VALUE);
        } else {
            item.setQuantity(item.getQuantity() + qty);
        }

        CartItem saved = cartItemRepository.save(item);

        Optional<ProductVariant> pvOpt = productVariantRepository.findById(variantId);

        CartItemDTO dto = cartItemMapper.toDto(saved);
        if (pvOpt.isPresent()) {
            ProductVariant pv = pvOpt.get();
            dto.setTotalPrice(pv.getPrice().multiply(BigDecimal.valueOf(pv.getStockQuantity())));
            dto.setVariantStock(pv.getStockQuantity());
        }
        return dto;
    }
}
