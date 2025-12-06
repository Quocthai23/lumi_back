package com.lumiere.app.service.impl;

import com.lumiere.app.domain.Customer;
import com.lumiere.app.domain.enumeration.CustomerTier;
import com.lumiere.app.repository.CustomerRepository;
import com.lumiere.app.service.LoyaltyService;
import com.lumiere.app.service.dto.LoyaltyTierDTO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing Loyalty Tier information.
 */
@Service
@Transactional
public class LoyaltyServiceImpl implements LoyaltyService {

    private static final Logger LOG = LoggerFactory.getLogger(LoyaltyServiceImpl.class);

    private final CustomerRepository customerRepository;

    public LoyaltyServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LoyaltyTierDTO> getLoyaltyTierByUserId(Long userId) {
        LOG.debug("Request to get loyalty tier for userId: {}", userId);

        Optional<Customer> customerOpt = customerRepository.findByUserId(userId);
        if (customerOpt.isEmpty()) {
            LOG.warn("Customer not found for userId: {}", userId);
            return Optional.empty();
        }

        Customer customer = customerOpt.get();
        CustomerTier tier = customer.getTier() != null ? customer.getTier() : CustomerTier.BRONZE;
        Integer loyaltyPoints = customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0;

        List<String> benefits = getBenefitsForTier(tier);
        String tierName = tier.name();

        LoyaltyTierDTO dto = new LoyaltyTierDTO(tier, tierName, loyaltyPoints, benefits);
        return Optional.of(dto);
    }

    /**
     * Lấy danh sách benefits theo tier.
     *
     * @param tier tier của khách hàng
     * @return danh sách benefits
     */
    private List<String> getBenefitsForTier(CustomerTier tier) {
        List<String> benefits = new ArrayList<>();

        switch (tier) {
            case BRONZE:
                benefits.addAll(
                    Arrays.asList(
                        "Tích điểm 1% cho mỗi đơn hàng",
                        "Ưu đãi độc quyền vào ngày sinh nhật",
                        "Tiếp cận các sự kiện giảm giá sớm"
                    )
                );
                break;
            case SILVER:
                benefits.addAll(
                    Arrays.asList(
                        "Tất cả quyền lợi của hạng BRONZE",
                        "Tích điểm 1.5% cho mỗi đơn hàng",
                        "Phí vận chuyển chỉ 20.000đ",
                        "Quà tặng đặc biệt mỗi quý"
                    )
                );
                break;
            case GOLD:
                benefits.addAll(
                    Arrays.asList(
                        "Tất cả quyền lợi của hạng SILVER",
                        "Tích điểm 2% cho mỗi đơn hàng",
                        "Miễn phí vận chuyển cho mọi đơn hàng",
                        "Hỗ trợ khách hàng ưu tiên 24/7",
                        "Sản phẩm độc quyền chỉ dành cho thành viên GOLD"
                    )
                );
                break;
            default:
                // Nếu tier không xác định, trả về benefits của BRONZE
                benefits.addAll(
                    Arrays.asList(
                        "Tích điểm 1% cho mỗi đơn hàng",
                        "Ưu đãi độc quyền vào ngày sinh nhật",
                        "Tiếp cận các sự kiện giảm giá sớm"
                    )
                );
                break;
        }

        return benefits;
    }
}

