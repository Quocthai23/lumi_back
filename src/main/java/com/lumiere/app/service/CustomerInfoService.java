package com.lumiere.app.service;

import com.lumiere.app.service.dto.CustomerInfoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CustomerInfoService {

    CustomerInfoDTO save(CustomerInfoDTO customerInfoDTO);

    Page<CustomerInfoDTO> findAll(Pageable pageable);

    Optional<CustomerInfoDTO> findOne(Long id);

    void delete(Long id);

    /**
     * Tìm tất cả thông tin địa chỉ của khách hàng.
     */
    List<CustomerInfoDTO> findAllByCustomerId(Long customerId);

    /**
     * Đặt địa chỉ làm mặc định.
     */
    CustomerInfoDTO setAsDefault(Long id, Long customerId);
}
