package com.lumiere.app.service;

import com.lumiere.app.domain.Customer;
import com.lumiere.app.domain.enumeration.CustomerTier;
import com.lumiere.app.repository.CustomerRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service để xử lý nâng cấp tier cho khách hàng dựa trên điểm tích lũy.
 */
@Service
@Transactional
public class LoyaltyTierUpgradeService {

    private static final Logger LOG = LoggerFactory.getLogger(LoyaltyTierUpgradeService.class);

    private final CustomerRepository customerRepository;

    // Ngưỡng điểm để nâng cấp tier
    private static final int SILVER_MIN_POINTS = 1000;
    private static final int GOLD_MIN_POINTS = 5000;

    public LoyaltyTierUpgradeService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Scheduled job chạy mỗi ngày lúc 2:00 AM để kiểm tra và nâng cấp tier cho khách hàng.
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void upgradeCustomerTiers() {
        LOG.info("Starting scheduled job to upgrade customer tiers");
        
        List<Customer> customers = customerRepository.findAll();
        int upgradedCount = 0;

        for (Customer customer : customers) {
            if (upgradeCustomerTier(customer)) {
                upgradedCount++;
            }
        }

        LOG.info("Completed tier upgrade job. Upgraded {} customers", upgradedCount);
    }

    /**
     * Nâng cấp tier cho một khách hàng dựa trên điểm tích lũy.
     *
     * @param customer khách hàng cần kiểm tra
     * @return true nếu đã nâng cấp, false nếu không
     */
    public boolean upgradeCustomerTier(Customer customer) {
        if (customer == null || customer.getLoyaltyPoints() == null) {
            return false;
        }

        Integer points = customer.getLoyaltyPoints();
        CustomerTier currentTier = customer.getTier();
        CustomerTier newTier = calculateTier(points);

        if (newTier != currentTier) {
            customer.setTier(newTier);
            customerRepository.save(customer);
            LOG.info(
                "Upgraded customer {} from {} to {} tier (points: {})",
                customer.getId(),
                currentTier != null ? currentTier : "NONE",
                newTier,
                points
            );
            return true;
        }

        return false;
    }

    /**
     * Tính tier dựa trên điểm tích lũy.
     *
     * @param points điểm tích lũy
     * @return tier tương ứng
     */
    private CustomerTier calculateTier(Integer points) {
        if (points == null || points < 0) {
            return CustomerTier.BRONZE;
        }

        if (points >= GOLD_MIN_POINTS) {
            return CustomerTier.GOLD;
        } else if (points >= SILVER_MIN_POINTS) {
            return CustomerTier.SILVER;
        } else {
            return CustomerTier.BRONZE;
        }
    }
}

