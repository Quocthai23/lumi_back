package com.lumiere.app.service.impl;

import com.lumiere.app.domain.Customer;
import com.lumiere.app.domain.CustomerVoucher;
import com.lumiere.app.domain.Voucher;
import com.lumiere.app.repository.CustomerRepository;
import com.lumiere.app.repository.CustomerVoucherRepository;
import com.lumiere.app.repository.VoucherRepository;
import com.lumiere.app.service.CustomerVoucherService;
import com.lumiere.app.service.dto.CustomerVoucherDTO;
import com.lumiere.app.service.dto.VoucherDTO;
import com.lumiere.app.service.mapper.VoucherMapper;
import java.time.Instant;
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
    private final VoucherRepository voucherRepository;
    private final VoucherMapper voucherMapper;

    public CustomerVoucherServiceImpl(
        CustomerVoucherRepository customerVoucherRepository,
        CustomerRepository customerRepository,
        VoucherRepository voucherRepository,
        VoucherMapper voucherMapper
    ) {
        this.customerVoucherRepository = customerVoucherRepository;
        this.customerRepository = customerRepository;
        this.voucherRepository = voucherRepository;
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

    @Override
    @Transactional
    public CustomerVoucherDTO claimVoucher(Long userId, Long voucherId) {
        LOG.debug("Request to claim voucher {} for userId: {}", voucherId, userId);

        // Tìm customer theo userId
        Customer customer = customerRepository
            .findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("Customer not found for user: " + userId));

        // Tìm voucher
        Voucher voucher = voucherRepository
            .findById(voucherId)
            .orElseThrow(() -> new IllegalArgumentException("Voucher not found: " + voucherId));

        // Kiểm tra xem customer đã claim voucher này chưa
        if (customerVoucherRepository.findByCustomerIdAndVoucherId(customer.getId(), voucherId).isPresent()) {
            throw new IllegalArgumentException("Bạn đã nhận mã giảm giá này rồi.");
        }

        // Kiểm tra voucher có đang active không
        if (voucher.getStatus() != com.lumiere.app.domain.enumeration.VoucherStatus.ACTIVE) {
            throw new IllegalArgumentException("Mã giảm giá này không khả dụng.");
        }

        // Kiểm tra thời gian hiệu lực
        Instant now = Instant.now();
        if (voucher.getStartDate() != null && now.isBefore(voucher.getStartDate())) {
            throw new IllegalArgumentException("Mã giảm giá chưa có hiệu lực.");
        }
        if (voucher.getEndDate() != null && now.isAfter(voucher.getEndDate())) {
            throw new IllegalArgumentException("Mã giảm giá đã hết hạn.");
        }

        // Kiểm tra giới hạn sử dụng
        if (voucher.getUsageLimit() != null && voucher.getUsageLimit() > 0) {
            if (voucher.getUsageCount() != null && voucher.getUsageCount() >= voucher.getUsageLimit()) {
                throw new IllegalArgumentException("Mã giảm giá đã hết lượt sử dụng.");
            }
        }

        // Tạo CustomerVoucher
        CustomerVoucher customerVoucher = new CustomerVoucher();
        customerVoucher.setCustomer(customer);
        customerVoucher.setVoucher(voucher);
        customerVoucher.setGiftedAt(Instant.now());
        customerVoucher.setUsed(false);
        // Không set quarter vì đây là claim tự do, không phải voucher quý

        customerVoucher = customerVoucherRepository.save(customerVoucher);

        return toDto(customerVoucher);
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

