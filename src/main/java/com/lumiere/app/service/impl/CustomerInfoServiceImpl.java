package com.lumiere.app.service.impl;

import com.lumiere.app.domain.CustomerInfo;
import com.lumiere.app.repository.CustomerInfoRepository;
import com.lumiere.app.service.CustomerInfoService;
import com.lumiere.app.service.dto.CustomerInfoDTO;
import com.lumiere.app.service.mapper.CustomerInfoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
}
