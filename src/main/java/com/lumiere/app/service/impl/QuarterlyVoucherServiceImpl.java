package com.lumiere.app.service.impl;

import com.lumiere.app.domain.Customer;
import com.lumiere.app.domain.CustomerVoucher;
import com.lumiere.app.domain.Voucher;
import com.lumiere.app.domain.enumeration.CustomerTier;
import com.lumiere.app.domain.enumeration.VoucherStatus;
import com.lumiere.app.domain.enumeration.VoucherType;
import com.lumiere.app.repository.CustomerRepository;
import com.lumiere.app.repository.CustomerVoucherRepository;
import com.lumiere.app.repository.VoucherRepository;
import com.lumiere.app.service.QuarterlyVoucherService;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing quarterly voucher distribution.
 */
@Service
@Transactional
public class QuarterlyVoucherServiceImpl implements QuarterlyVoucherService {

    private static final Logger LOG = LoggerFactory.getLogger(QuarterlyVoucherServiceImpl.class);

    private final CustomerRepository customerRepository;
    private final VoucherRepository voucherRepository;
    private final CustomerVoucherRepository customerVoucherRepository;

    // Cấu hình voucher theo tier
    private static final BigDecimal BRONZE_VOUCHER_VALUE = BigDecimal.valueOf(50000); // 50,000 VND
    private static final BigDecimal SILVER_VOUCHER_VALUE = BigDecimal.valueOf(100000); // 100,000 VND
    private static final BigDecimal GOLD_VOUCHER_VALUE = BigDecimal.valueOf(200000); // 200,000 VND

    public QuarterlyVoucherServiceImpl(
        CustomerRepository customerRepository,
        VoucherRepository voucherRepository,
        CustomerVoucherRepository customerVoucherRepository
    ) {
        this.customerRepository = customerRepository;
        this.voucherRepository = voucherRepository;
        this.customerVoucherRepository = customerVoucherRepository;
    }

    /**
     * Scheduled job chạy vào ngày đầu tiên của mỗi quý (1/1, 1/4, 1/7, 1/10) lúc 0:00 AM.
     */
    @Scheduled(cron = "0 0 0 1 1,4,7,10 ?")
    public void scheduleQuarterlyVoucherDistribution() {
        LOG.info("Starting scheduled quarterly voucher distribution");
        distributeQuarterlyVouchers();
    }

    @Override
    @Transactional
    public void distributeQuarterlyVouchers() {
        String currentQuarter = getCurrentQuarter();
        LOG.info("Distributing quarterly vouchers for quarter: {}", currentQuarter);

        // Lấy tất cả khách hàng
        List<Customer> customers = customerRepository.findAll();
        int distributedCount = 0;
        int skippedCount = 0;

        for (Customer customer : customers) {
            try {
                // Kiểm tra xem khách hàng đã nhận voucher trong quý này chưa
                if (customerVoucherRepository.existsByCustomerIdAndQuarter(customer.getId(), currentQuarter)) {
                    LOG.debug("Customer {} already received voucher for quarter {}", customer.getId(), currentQuarter);
                    skippedCount++;
                    continue;
                }

                // Tạo và tặng voucher theo tier
                CustomerTier tier = customer.getTier() != null ? customer.getTier() : CustomerTier.BRONZE;
                Voucher voucher = createVoucherForTier(tier, currentQuarter, customer.getId());
                voucher = voucherRepository.save(voucher);

                // Lưu vào CustomerVoucher
                CustomerVoucher customerVoucher = new CustomerVoucher();
                customerVoucher.setCustomer(customer);
                customerVoucher.setVoucher(voucher);
                customerVoucher.setGiftedAt(Instant.now());
                customerVoucher.setQuarter(currentQuarter);
                customerVoucher.setUsed(false);
                customerVoucherRepository.save(customerVoucher);

                distributedCount++;
                LOG.debug("Distributed voucher {} to customer {} (tier: {})", voucher.getCode(), customer.getId(), tier);
            } catch (Exception e) {
                LOG.error("Error distributing voucher to customer {}: {}", customer.getId(), e.getMessage(), e);
            }
        }

        LOG.info(
            "Completed quarterly voucher distribution. Distributed: {}, Skipped: {}, Total customers: {}",
            distributedCount,
            skippedCount,
            customers.size()
        );
    }

    /**
     * Tạo voucher cho tier cụ thể.
     *
     * @param tier tier của khách hàng
     * @param quarter quý hiện tại
     * @param customerId ID của khách hàng
     * @return voucher đã tạo
     */
    private Voucher createVoucherForTier(CustomerTier tier, String quarter, Long customerId) {
        Voucher voucher = new Voucher();

        // Tạo code voucher duy nhất
        String voucherCode = generateVoucherCode(tier, quarter, customerId);
        voucher.setCode(voucherCode);

        // Xác định giá trị voucher theo tier
        BigDecimal voucherValue;
        switch (tier) {
            case BRONZE:
                voucherValue = BRONZE_VOUCHER_VALUE;
                break;
            case SILVER:
                voucherValue = SILVER_VOUCHER_VALUE;
                break;
            case GOLD:
                voucherValue = GOLD_VOUCHER_VALUE;
                break;
            default:
                voucherValue = BRONZE_VOUCHER_VALUE;
                break;
        }

        voucher.setType(VoucherType.FIXED_AMOUNT);
        voucher.setValue(voucherValue);
        voucher.setStatus(VoucherStatus.ACTIVE);

        // Voucher có hiệu lực trong 3 tháng (cả quý)
        Instant now = Instant.now();
        voucher.setStartDate(now);
        voucher.setEndDate(now.plusSeconds(90L * 24 * 60 * 60)); // 90 ngày

        // Mỗi voucher chỉ dùng được 1 lần
        voucher.setUsageLimit(1);
        voucher.setUsageCount(0);

        return voucher;
    }

    /**
     * Tạo mã voucher duy nhất.
     *
     * @param tier tier của khách hàng
     * @param quarter quý
     * @param customerId ID khách hàng
     * @return mã voucher
     */
    private String generateVoucherCode(CustomerTier tier, String quarter, Long customerId) {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return String.format("TIER-%s-%s-%s-%s", tier.name(), quarter.replace("-", ""), customerId, uniqueId);
    }

    /**
     * Lấy quý hiện tại (format: "2024-Q1").
     *
     * @return quý hiện tại
     */
    private String getCurrentQuarter() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();

        String quarter;
        if (month >= 1 && month <= 3) {
            quarter = "Q1";
        } else if (month >= 4 && month <= 6) {
            quarter = "Q2";
        } else if (month >= 7 && month <= 9) {
            quarter = "Q3";
        } else {
            quarter = "Q4";
        }

        return year + "-" + quarter;
    }
}

