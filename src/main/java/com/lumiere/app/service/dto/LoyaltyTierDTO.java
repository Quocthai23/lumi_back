package com.lumiere.app.service.dto;

import com.lumiere.app.domain.enumeration.CustomerTier;
import java.io.Serializable;
import java.util.List;

/**
 * DTO cho thông tin Loyalty Tier của khách hàng.
 */
public class LoyaltyTierDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private CustomerTier tier;
    private String tierName;
    private Integer loyaltyPoints;
    private List<String> benefits;

    public LoyaltyTierDTO() {}

    public LoyaltyTierDTO(CustomerTier tier, String tierName, Integer loyaltyPoints, List<String> benefits) {
        this.tier = tier;
        this.tierName = tierName;
        this.loyaltyPoints = loyaltyPoints;
        this.benefits = benefits;
    }

    public CustomerTier getTier() {
        return tier;
    }

    public void setTier(CustomerTier tier) {
        this.tier = tier;
    }

    public String getTierName() {
        return tierName;
    }

    public void setTierName(String tierName) {
        this.tierName = tierName;
    }

    public Integer getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(Integer loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public List<String> getBenefits() {
        return benefits;
    }

    public void setBenefits(List<String> benefits) {
        this.benefits = benefits;
    }

    @Override
    public String toString() {
        return "LoyaltyTierDTO{" + "tier=" + tier + ", tierName='" + tierName + '\'' + ", loyaltyPoints=" + loyaltyPoints + ", benefits=" + benefits + '}';
    }
}

