package com.lumiere.app.service.impl;

import com.lumiere.app.domain.Customer;
import com.lumiere.app.domain.CustomerVoucher;
import com.lumiere.app.repository.CustomerRepository;
import com.lumiere.app.repository.CustomerVoucherRepository;
import com.lumiere.app.service.CustomerVoucherService;
import com.lumiere.app.service.dto.CustomerVoucherDTO;
import com.lumiere.app.service.dto.VoucherDTO;
import com.lumiere.app.service.mapper.VoucherMapper;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing CustomerVoucher.
 */
@Service
@Transactional
public class CustomerVoucherServiceImpl implements CustomerVoucherService {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerVoucherServiceImpl.class);

    private final CustomerVoucherRepository customerVoucherRepository;
    private final CustomerRepository customerRepository;
    private final VoucherMapper voucherMapper;

    public CustomerVoucherServiceImpl(
        CustomerVoucherRepository customerVoucherRepository,
        CustomerRepository customerRepository,
        VoucherMapper voucherMapper
    ) {
        this.customerVoucherRepository = customerVoucherRepository;
        this.customerRepository = customerRepository;
        this.voucherMapper = voucherMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerVoucherDTO> getVouchersByUserId(Long userId) {
        LOG.debug("Request to get vouchers for userId: {}", userId);

        Customer customer = customerRepository
            .findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("Customer not found for user: " + userId));

        List<CustomerVoucher> customerVouchers = customerVoucherRepository.findByCustomerId(customer.getId());

        return customerVouchers.stream().map(this::toDto).collect(Collectors.toList());
    }

    private CustomerVoucherDTO toDto(CustomerVoucher customerVoucher) {
        CustomerVoucherDTO dto = new CustomerVoucherDTO();
        dto.setId(customerVoucher.getId());
        dto.setGiftedAt(customerVoucher.getGiftedAt());
        dto.setQuarter(customerVoucher.getQuarter());
        dto.setUsed(customerVoucher.getUsed() != null ? customerVoucher.getUsed() : false);

        if (customerVoucher.getVoucher() != null) {
            VoucherDTO voucherDTO = voucherMapper.toDto(customerVoucher.getVoucher());
            dto.setVoucher(voucherDTO);
        }

        return dto;
    }
}

