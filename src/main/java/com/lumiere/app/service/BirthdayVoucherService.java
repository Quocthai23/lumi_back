package com.lumiere.app.service;

import com.lumiere.app.domain.Customer;
import com.lumiere.app.domain.CustomerVoucher;
import com.lumiere.app.domain.Voucher;
import com.lumiere.app.domain.enumeration.VoucherStatus;
import com.lumiere.app.domain.enumeration.VoucherType;
import com.lumiere.app.repository.CustomerRepository;
import com.lumiere.app.repository.CustomerVoucherRepository;
import com.lumiere.app.repository.VoucherRepository;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service để tặng voucher cho khách hàng vào dịp sinh nhật.
 */
@Service
@Transactional
public class BirthdayVoucherService {

    private static final Logger LOG = LoggerFactory.getLogger(BirthdayVoucherService.class);

    private final CustomerRepository customerRepository;
    private final VoucherRepository voucherRepository;
    private final CustomerVoucherRepository customerVoucherRepository;
    private final SecureRandom random = new SecureRandom();

    // Cấu hình voucher sinh nhật
    private static final BigDecimal BIRTHDAY_VOUCHER_VALUE = new BigDecimal("10"); // 10% giảm giá
    private static final int VOUCHER_VALID_DAYS = 30; // Voucher có hiệu lực 30 ngày
    private static final int VOUCHER_CODE_LENGTH = 8; // Độ dài mã voucher

    public BirthdayVoucherService(
        CustomerRepository customerRepository,
        VoucherRepository voucherRepository,
        CustomerVoucherRepository customerVoucherRepository
    ) {
        this.customerRepository = customerRepository;
        this.voucherRepository = voucherRepository;
        this.customerVoucherRepository = customerVoucherRepository;
    }

    /**
     * Scheduled job chạy mỗi ngày lúc 3:00 AM để tặng voucher cho khách hàng có sinh nhật.
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void giftBirthdayVouchers() {
        LOG.info("Starting scheduled job to gift birthday vouchers");

        LocalDate today = LocalDate.now();
        int month = today.getMonthValue();
        int day = today.getDayOfMonth();
        int year = today.getYear();

        // Tìm tất cả khách hàng có sinh nhật hôm nay
        List<Customer> birthdayCustomers = customerRepository.findByBirthdayMonthAndDay(month, day);

        if (birthdayCustomers.isEmpty()) {
            LOG.info("No customers with birthday today");
            return;
        }

        LOG.info("Found {} customers with birthday today", birthdayCustomers.size());

        int giftedCount = 0;
        int skippedCount = 0;

        for (Customer customer : birthdayCustomers) {
            try {
                if (giftBirthdayVoucherToCustomer(customer, year)) {
                    giftedCount++;
                } else {
                    skippedCount++;
                }
            } catch (Exception e) {
                LOG.error("Error gifting voucher to customer {}: {}", customer.getId(), e.getMessage(), e);
                skippedCount++;
            }
        }

        LOG.info("Completed birthday voucher job. Gifted: {}, Skipped: {}", giftedCount, skippedCount);
    }

    /**
     * Tặng voucher sinh nhật cho một khách hàng.
     *
     * @param customer khách hàng
     * @param year năm hiện tại
     * @return true nếu đã tặng thành công, false nếu đã tặng trong năm này
     */
    public boolean giftBirthdayVoucherToCustomer(Customer customer, int year) {
        if (customer == null || customer.getBirthday() == null) {
            LOG.warn("Customer or birthday is null for customer: {}", customer != null ? customer.getId() : "null");
            return false;
        }

        // Kiểm tra xem đã tặng voucher trong năm này chưa
        String yearString = String.valueOf(year);
        boolean alreadyGifted = customerVoucherRepository.existsByCustomerIdAndYear(
            customer.getId(),
            yearString
        );

        if (alreadyGifted) {
            LOG.debug("Customer {} already received birthday voucher in year {}", customer.getId(), year);
            return false;
        }

        // Tạo voucher mới
        Voucher voucher = createBirthdayVoucher(customer);
        voucher = voucherRepository.save(voucher);

        // Tạo CustomerVoucher để gán voucher cho customer
        CustomerVoucher customerVoucher = new CustomerVoucher();
        customerVoucher.setCustomer(customer);
        customerVoucher.setVoucher(voucher);
        customerVoucher.setGiftedAt(Instant.now());
        customerVoucher.setQuarter(yearString);
        customerVoucher.setUsed(false);

        customerVoucherRepository.save(customerVoucher);

        LOG.info(
            "Gifted birthday voucher {} to customer {} (ID: {})",
            voucher.getCode(),
            customer.getFirstName() + " " + customer.getLastName(),
            customer.getId()
        );

        return true;
    }

    /**
     * Tạo voucher sinh nhật mới.
     *
     * @param customer khách hàng
     * @return voucher mới
     */
    private Voucher createBirthdayVoucher(Customer customer) {
        Voucher voucher = new Voucher();

        // Tạo mã voucher unique
        String voucherCode = generateUniqueVoucherCode();
        voucher.setCode(voucherCode);

        // Cấu hình voucher
        voucher.setType(VoucherType.PERCENTAGE);
        voucher.setValue(BIRTHDAY_VOUCHER_VALUE);
        voucher.setStatus(VoucherStatus.ACTIVE);

        // Thời gian hiệu lực
        Instant now = Instant.now();
        voucher.setStartDate(now);
        voucher.setEndDate(now.plusSeconds(VOUCHER_VALID_DAYS * 24 * 60 * 60));

        // Giới hạn sử dụng: 1 lần
        voucher.setUsageLimit(1);
        voucher.setUsageCount(0);

        return voucher;
    }

    /**
     * Tạo mã voucher unique.
     *
     * @return mã voucher unique
     */
    private String generateUniqueVoucherCode() {
        String code;
        int attempts = 0;
        int maxAttempts = 10;

        do {
            // Tạo mã voucher: BIRTH-XXXX (BIRTH + 4 ký tự random)
            code = "BIRTH-" + generateRandomCode(VOUCHER_CODE_LENGTH);
            attempts++;

            if (attempts >= maxAttempts) {
                // Nếu không tạo được mã unique sau nhiều lần, thêm timestamp
                code = "BIRTH-" + generateRandomCode(VOUCHER_CODE_LENGTH) + "-" + System.currentTimeMillis() % 10000;
                break;
            }
        } while (voucherRepository.findByCode(code).isPresent());

        return code;
    }

    /**
     * Tạo mã random.
     *
     * @param length độ dài mã
     * @return mã random
     */
    private String generateRandomCode(int length) {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}

