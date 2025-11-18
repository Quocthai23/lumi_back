package com.lumiere.app.service.impl;

import com.lumiere.app.domain.CartItem;
import com.lumiere.app.repository.CartItemRepository;
import com.lumiere.app.service.CartItemService;
import com.lumiere.app.service.dto.CartItemDTO;
import com.lumiere.app.service.mapper.CartItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

@Service
@Transactional
public class CartItemServiceImpl implements CartItemService {

    private final Logger log = LoggerFactory.getLogger(CartItemServiceImpl.class);

    private final CartItemRepository cartItemRepository;

    private final CartItemMapper cartItemMapper;

    public CartItemServiceImpl(CartItemRepository cartItemRepository, CartItemMapper cartItemMapper) {
        this.cartItemRepository = cartItemRepository;
        this.cartItemMapper = cartItemMapper;
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
}
