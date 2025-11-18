package com.lumiere.app.service;

import com.lumiere.app.service.dto.CartItemDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface CartItemService {

    CartItemDTO save(CartItemDTO cartItemDTO);

    CartItemDTO update(CartItemDTO cartItemDTO);

    Optional<CartItemDTO> partialUpdate(CartItemDTO cartItemDTO);

    Page<CartItemDTO> findAll(Pageable pageable);

    Optional<CartItemDTO> findOne(Long id);

    void delete(Long id);

    @Transactional(readOnly = true)
    Page<CartItemDTO> findAllByCustomerId(Long customerId, Pageable pageable);
}
