package com.lumiere.app.service.impl;

import com.lumiere.app.domain.CustomerVoucher;
import com.lumiere.app.domain.Voucher;
import com.lumiere.app.domain.enumeration.NotificationType;
import com.lumiere.app.domain.enumeration.VoucherStatus;
import com.lumiere.app.domain.enumeration.VoucherType;
import com.lumiere.app.repository.CustomerVoucherRepository;
import com.lumiere.app.repository.VoucherRepository;
import com.lumiere.app.service.VoucherService;
import com.lumiere.app.service.kafka.NotificationProducerService;
import com.lumiere.app.service.dto.VoucherCalculateRequestDTO;
import com.lumiere.app.service.dto.VoucherCalculateResponseDTO;
import com.lumiere.app.service.dto.VoucherDTO;
import com.lumiere.app.service.mapper.VoucherMapper;
import java.math.BigDecimal;
import java.time.Instant;
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
 * Service Implementation for managing {@link com.lumiere.app.domain.Voucher}.
 */
@Service
@Transactional
public class VoucherServiceImpl implements VoucherService {

    private static final Logger LOG = LoggerFactory.getLogger(VoucherServiceImpl.class);

    private final VoucherRepository voucherRepository;

    private final VoucherMapper voucherMapper;

    private final CustomerVoucherRepository customerVoucherRepository;

    private final NotificationProducerService notificationProducerService;

    public VoucherServiceImpl(
        VoucherRepository voucherRepository,
        VoucherMapper voucherMapper,
        CustomerVoucherRepository customerVoucherRepository,
        NotificationProducerService notificationProducerService
    ) {
        this.voucherRepository = voucherRepository;
        this.voucherMapper = voucherMapper;
        this.customerVoucherRepository = customerVoucherRepository;
        this.notificationProducerService = notificationProducerService;
    }

    @Override
    public VoucherDTO save(VoucherDTO voucherDTO) {
        LOG.debug("Request to save Voucher : {}", voucherDTO);
        Voucher voucher = voucherMapper.toEntity(voucherDTO);
        boolean isNew = voucher.getId() == null;
        voucher = voucherRepository.save(voucher);
        
        // Gửi notification cho tất cả customers về voucher mới
        if (isNew && voucher.getStatus() == VoucherStatus.ACTIVE) {
            // Note: Trong thực tế, có thể cần gửi cho từng customer hoặc broadcast
            // Ở đây tôi sẽ gửi notification cho admin để admin biết có voucher mới
            // và có thể gửi email cho customers
            String adminMessage = String.format("Voucher mới đã được tạo: %s (Giá trị: %s)", 
                voucher.getCode(), voucher.getValue());
            notificationProducerService.sendAdminNotification(
                NotificationType.NEW_VOUCHER,
                adminMessage,
                "/admin/vouchers/" + voucher.getId()
            );
        }
        
        return voucherMapper.toDto(voucher);
    }

    @Override
    public VoucherDTO update(VoucherDTO voucherDTO) {
        LOG.debug("Request to update Voucher : {}", voucherDTO);
        Voucher voucher = voucherMapper.toEntity(voucherDTO);
        voucher = voucherRepository.save(voucher);
        return voucherMapper.toDto(voucher);
    }

    @Override
    public Optional<VoucherDTO> partialUpdate(VoucherDTO voucherDTO) {
        LOG.debug("Request to partially update Voucher : {}", voucherDTO);

        return voucherRepository
            .findById(voucherDTO.getId())
            .map(existingVoucher -> {
                voucherMapper.partialUpdate(existingVoucher, voucherDTO);

                return existingVoucher;
            })
            .map(voucherRepository::save)
            .map(voucherMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoucherDTO> findAll() {
        LOG.debug("Request to get all Vouchers");
        return voucherRepository.findAll().stream().map(voucherMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VoucherDTO> findAllAvailable(Pageable pageable) {
        LOG.debug("Request to get all available Vouchers with pagination");
        Instant now = Instant.now();
        Page<Voucher> vouchers = voucherRepository.findAllAvailable(VoucherStatus.ACTIVE, now, pageable);
        return vouchers.map(voucherMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VoucherDTO> findOne(Long id) {
        LOG.debug("Request to get Voucher : {}", id);
        return voucherRepository.findById(id).map(voucherMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Voucher : {}", id);
        voucherRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VoucherDTO> findByCode(String code) {
        LOG.debug("Request to get Voucher by code: {}", code);
        return voucherRepository.findByCode(code).map(voucherMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Voucher validateVoucher(String voucherCode, BigDecimal orderAmount) {
        LOG.debug("Request to validate voucher: {} for order amount: {}", voucherCode, orderAmount);

        if (voucherCode == null || voucherCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã voucher không được để trống");
        }

        Voucher voucher = voucherRepository.findByCode(voucherCode.trim().toUpperCase())
            .orElseThrow(() -> new IllegalArgumentException("Mã voucher không tồn tại: " + voucherCode));

        // Kiểm tra trạng thái
        if (voucher.getStatus() != VoucherStatus.ACTIVE) {
            throw new IllegalArgumentException("Voucher không khả dụng. Trạng thái: " + voucher.getStatus());
        }

        // Kiểm tra thời gian hiệu lực
        Instant now = Instant.now();
        if (voucher.getStartDate() != null && now.isBefore(voucher.getStartDate())) {
            throw new IllegalArgumentException("Voucher chưa có hiệu lực. Bắt đầu từ: " + voucher.getStartDate());
        }
        if (voucher.getEndDate() != null && now.isAfter(voucher.getEndDate())) {
            throw new IllegalArgumentException("Voucher đã hết hạn. Kết thúc vào: " + voucher.getEndDate());
        }

        // Kiểm tra số lần sử dụng
        if (voucher.getUsageLimit() != null) {
            int currentUsage = voucher.getUsageCount() != null ? voucher.getUsageCount() : 0;
            if (currentUsage >= voucher.getUsageLimit()) {
                throw new IllegalArgumentException("Voucher đã hết lượt sử dụng");
            }
        }

        // Kiểm tra giá trị đơn hàng tối thiểu (nếu có thể thêm sau)
        // Có thể thêm trường minOrderAmount vào Voucher entity nếu cần

        return voucher;
    }

    @Override
    public BigDecimal calculateDiscountAmount(Voucher voucher, BigDecimal orderAmount) {
        if (voucher == null || orderAmount == null || orderAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount = BigDecimal.ZERO;

        if (voucher.getType() == VoucherType.PERCENTAGE) {
            // Giảm giá theo phần trăm
            BigDecimal percentage = voucher.getValue();
            if (percentage.compareTo(BigDecimal.valueOf(100)) > 0) {
                percentage = BigDecimal.valueOf(100); // Tối đa 100%
            }
            discount = orderAmount.multiply(percentage).divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
        } else if (voucher.getType() == VoucherType.FIXED_AMOUNT) {
            // Giảm giá cố định
            discount = voucher.getValue();
            // Đảm bảo không giảm quá tổng tiền đơn hàng
            if (discount.compareTo(orderAmount) > 0) {
                discount = orderAmount;
            }
        }

        return discount;
    }

    @Override
    @Transactional
    public void applyVoucher(Voucher voucher) {
        LOG.debug("Request to apply voucher: {}", voucher.getCode());

        // Tăng usage count
        int currentUsage = voucher.getUsageCount() != null ? voucher.getUsageCount() : 0;
        voucher.setUsageCount(currentUsage + 1);

        // Nếu đã đạt giới hạn, cập nhật trạng thái
        if (voucher.getUsageLimit() != null && voucher.getUsageCount() >= voucher.getUsageLimit()) {
            voucher.setStatus(VoucherStatus.INACTIVE);
        }

        voucherRepository.save(voucher);
        LOG.info("Applied voucher: {}, new usage count: {}", voucher.getCode(), voucher.getUsageCount());
    }

    @Override
    @Transactional(readOnly = true)
    public VoucherCalculateResponseDTO calculateDiscount(VoucherCalculateRequestDTO request, Long customerId) {
        LOG.debug("Request to calculate discount for voucher: {} by customer: {}", request.getVoucherCode(), customerId);

        // Nếu không có mã voucher, trả về discount = 0
        if (request.getVoucherCode() == null || request.getVoucherCode().trim().isEmpty()) {
            VoucherCalculateResponseDTO response = new VoucherCalculateResponseDTO();
            response.setDiscountAmount(BigDecimal.ZERO);
            response.setFinalAmount(request.getOrderAmount());
            response.setVoucher(null);

            LOG.info(
                "No voucher code provided, orderAmount: {}, discountAmount: 0, finalAmount: {}",
                request.getOrderAmount(),
                request.getOrderAmount()
            );

            return response;
        }

        // Validate voucher cơ bản (tồn tại, status, thời gian, số lần sử dụng)
        Voucher voucher = validateVoucher(request.getVoucherCode(), request.getOrderAmount());

        // Kiểm tra tư cách sử dụng voucher: Customer phải được tặng voucher này
        if (customerId != null) {
            Optional<CustomerVoucher> customerVoucherOpt = customerVoucherRepository.findByCustomerIdAndVoucherId(
                customerId,
                voucher.getId()
            );

            if (customerVoucherOpt.isEmpty()) {
                throw new IllegalArgumentException(
                    "Bạn chưa được tặng voucher này. Vui lòng kiểm tra lại mã voucher hoặc liên hệ hỗ trợ."
                );
            }

            CustomerVoucher customerVoucher = customerVoucherOpt.get();

            // Kiểm tra xem voucher đã được sử dụng chưa
            if (Boolean.TRUE.equals(customerVoucher.getUsed())) {
                throw new IllegalArgumentException("Voucher này đã được sử dụng. Mỗi voucher chỉ có thể sử dụng một lần.");
            }
        }

        // Tính số tiền giảm giá
        BigDecimal discountAmount = calculateDiscountAmount(voucher, request.getOrderAmount());
        BigDecimal finalAmount = request.getOrderAmount().subtract(discountAmount);

        // Tạo response
        VoucherCalculateResponseDTO response = new VoucherCalculateResponseDTO();
        response.setDiscountAmount(discountAmount);
        response.setFinalAmount(finalAmount);
        response.setVoucher(voucherMapper.toDto(voucher));

        LOG.info(
            "Calculated discount for voucher: {}, orderAmount: {}, discountAmount: {}, finalAmount: {}",
            request.getVoucherCode(),
            request.getOrderAmount(),
            discountAmount,
            finalAmount
        );

        return response;
    }

    @Override
    @Transactional
    public void markVoucherAsUsed(Long voucherId, Long customerId) {
        LOG.debug("Request to mark voucher {} as used by customer {}", voucherId, customerId);

        // Tìm CustomerVoucher
        CustomerVoucher customerVoucher = customerVoucherRepository
            .findByCustomerIdAndVoucherId(customerId, voucherId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Không tìm thấy voucher cho khách hàng này. Customer ID: " + customerId + ", Voucher ID: " + voucherId
            ));

        // Đánh dấu đã sử dụng
        customerVoucher.setUsed(true);
        customerVoucherRepository.save(customerVoucher);

        LOG.info("Marked voucher {} as used by customer {}", voucherId, customerId);
    }
}
