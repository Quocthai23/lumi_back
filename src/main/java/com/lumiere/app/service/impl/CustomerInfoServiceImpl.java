package com.lumiere.app.service.impl;

import com.lumiere.app.domain.CustomerInfo;
import com.lumiere.app.repository.CustomerInfoRepository;
import com.lumiere.app.service.CustomerInfoService;
import com.lumiere.app.service.dto.CustomerInfoDTO;
import com.lumiere.app.service.mapper.CustomerInfoMapper;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerInfoServiceImpl implements CustomerInfoService {

    private final Logger log = LoggerFactory.getLogger(CustomerInfoServiceImpl.class);

    private final CustomerInfoRepository customerInfoRepository;
    private final CustomerInfoMapper customerInfoMapper;

    public CustomerInfoServiceImpl(CustomerInfoRepository customerInfoRepository, CustomerInfoMapper customerInfoMapper) {
        this.customerInfoRepository = customerInfoRepository;
        this.customerInfoMapper = customerInfoMapper;
    }

    @Override
    public CustomerInfoDTO save(CustomerInfoDTO dto) {
        log.debug("Request to save CustomerInfo : {}", dto);
        CustomerInfo entity = customerInfoMapper.toEntity(dto);
        
        // Nếu địa chỉ này được đặt làm mặc định, bỏ mặc định của các địa chỉ khác
        if (Boolean.TRUE.equals(dto.getIsDefault()) && dto.getCustomerId() != null) {
            List<CustomerInfo> existingDefault = customerInfoRepository.findByCustomerId(dto.getCustomerId())
                .stream()
                .filter(ci -> Boolean.TRUE.equals(ci.getIsDefault()) && !ci.getId().equals(dto.getId()))
                .collect(Collectors.toList());
            
            for (CustomerInfo ci : existingDefault) {
                ci.setIsDefault(false);
                customerInfoRepository.save(ci);
            }
        }
        
        // Nếu đây là địa chỉ đầu tiên của khách hàng, tự động đặt làm mặc định
        if (dto.getCustomerId() != null) {
            List<CustomerInfo> existingInfos = customerInfoRepository.findByCustomerId(dto.getCustomerId());
            if (existingInfos.isEmpty() || (dto.getId() == null && existingInfos.stream().noneMatch(ci -> Boolean.TRUE.equals(ci.getIsDefault())))) {
                entity.setIsDefault(true);
            }
        }
        
        entity = customerInfoRepository.save(entity);
        return customerInfoMapper.toDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerInfoDTO> findAll(Pageable pageable) {
        log.debug("Request to get all CustomerInfos");
        return customerInfoRepository.findAll(pageable).map(customerInfoMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerInfoDTO> findOne(Long id) {
        log.debug("Request to get CustomerInfo : {}", id);
        return customerInfoRepository.findById(id).map(customerInfoMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete CustomerInfo : {}", id);
        customerInfoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerInfoDTO> findAllByCustomerId(Long customerId) {
        log.debug("Request to get all CustomerInfos by customerId : {}", customerId);
        return customerInfoRepository.findByCustomerId(customerId)
            .stream()
            .map(customerInfoMapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public CustomerInfoDTO setAsDefault(Long id, Long customerId) {
        log.debug("Request to set CustomerInfo {} as default for customer {}", id, customerId);
        
        // Bỏ mặc định của tất cả địa chỉ khác của khách hàng
        List<CustomerInfo> existingInfos = customerInfoRepository.findByCustomerId(customerId);
        for (CustomerInfo ci : existingInfos) {
            if (Boolean.TRUE.equals(ci.getIsDefault()) && !ci.getId().equals(id)) {
                ci.setIsDefault(false);
                customerInfoRepository.save(ci);
            }
        }
        
        // Đặt địa chỉ này làm mặc định
        CustomerInfo customerInfo = customerInfoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("CustomerInfo not found with id: " + id));
        
        if (!customerInfo.getCustomer().getId().equals(customerId)) {
            throw new IllegalArgumentException("CustomerInfo does not belong to the specified customer");
        }
        
        customerInfo.setIsDefault(true);
        customerInfo = customerInfoRepository.save(customerInfo);
        return customerInfoMapper.toDto(customerInfo);
    }
}
