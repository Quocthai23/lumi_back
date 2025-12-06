package com.lumiere.app.service;

import com.lumiere.app.service.dto.LoyaltyTierDTO;
import java.util.Optional;

/**
 * Service Interface for managing Loyalty Tier information.
 */
public interface LoyaltyService {
    /**
     * Lấy thông tin loyalty tier của khách hàng theo userId.
     *
     * @param userId ID của user
     * @return Optional chứa LoyaltyTierDTO nếu tìm thấy, empty nếu không
     */
    Optional<LoyaltyTierDTO> getLoyaltyTierByUserId(Long userId);
}

