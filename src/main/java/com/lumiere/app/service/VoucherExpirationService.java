package com.lumiere.app.service;

import com.lumiere.app.domain.Voucher;
import com.lumiere.app.domain.enumeration.NotificationType;
import com.lumiere.app.domain.enumeration.VoucherStatus;
import com.lumiere.app.repository.VoucherRepository;
import com.lumiere.app.service.kafka.NotificationProducerService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service để xử lý cập nhật trạng thái voucher hết hạn.
 */
@Service
@Transactional
public class VoucherExpirationService {

    private static final Logger LOG = LoggerFactory.getLogger(VoucherExpirationService.class);

    private final VoucherRepository voucherRepository;

    private final NotificationProducerService notificationProducerService;

    public VoucherExpirationService(
        VoucherRepository voucherRepository,
        NotificationProducerService notificationProducerService
    ) {
        this.voucherRepository = voucherRepository;
        this.notificationProducerService = notificationProducerService;
    }

    /**
     * Scheduled job chạy mỗi 5 phút để cập nhật trạng thái voucher hết hạn.
     * Chạy mỗi 5 phút = 300,000 milliseconds.
     */
    @Scheduled(fixedRate = 300000) // 5 phút = 5 * 60 * 1000 ms
    public void updateExpiredVouchers() {
        LOG.debug("Starting scheduled job to update expired vouchers");
        
        Instant now = Instant.now();
        List<Voucher> expiredVouchers = voucherRepository.findExpiredVouchers(VoucherStatus.ACTIVE, now);
        
        if (expiredVouchers.isEmpty()) {
            LOG.debug("No expired vouchers found");
        } else {
            LOG.info("Found {} expired vouchers to update", expiredVouchers.size());
            
            int updatedCount = 0;
            for (Voucher voucher : expiredVouchers) {
                try {
                    voucher.setStatus(VoucherStatus.INACTIVE);
                    voucherRepository.save(voucher);
                    updatedCount++;
                    LOG.debug("Updated voucher {} (code: {}) to INACTIVE", voucher.getId(), voucher.getCode());
                } catch (Exception e) {
                    LOG.error("Error updating voucher {}: {}", voucher.getId(), e.getMessage(), e);
                }
            }

            LOG.info("Completed expired voucher update job. Updated {} vouchers", updatedCount);
        }

        // Kiểm tra voucher sắp hết hạn (trong vòng 24 giờ) và gửi notification cho admin
        Instant tomorrow = now.plus(24, ChronoUnit.HOURS);
        List<Voucher> expiringSoonVouchers = voucherRepository.findVouchersExpiringBetween(
            VoucherStatus.ACTIVE, now, tomorrow
        );
        
        if (!expiringSoonVouchers.isEmpty()) {
            LOG.info("Found {} vouchers expiring soon", expiringSoonVouchers.size());
            for (Voucher voucher : expiringSoonVouchers) {
                try {
                    String adminMessage = String.format("Voucher %s sắp hết hạn trong vòng 24 giờ. Hết hạn: %s", 
                        voucher.getCode(), voucher.getEndDate());
                    notificationProducerService.sendAdminNotification(
                        NotificationType.VOUCHER_EXPIRING,
                        adminMessage,
                        "/admin/vouchers/" + voucher.getId()
                    );
                } catch (Exception e) {
                    LOG.error("Error sending notification for expiring voucher {}: {}", voucher.getId(), e.getMessage(), e);
                }
            }
        }
    }
}

