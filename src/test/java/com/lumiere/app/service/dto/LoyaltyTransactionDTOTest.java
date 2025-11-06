package com.lumiere.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumiere.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LoyaltyTransactionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(LoyaltyTransactionDTO.class);
        LoyaltyTransactionDTO loyaltyTransactionDTO1 = new LoyaltyTransactionDTO();
        loyaltyTransactionDTO1.setId(1L);
        LoyaltyTransactionDTO loyaltyTransactionDTO2 = new LoyaltyTransactionDTO();
        assertThat(loyaltyTransactionDTO1).isNotEqualTo(loyaltyTransactionDTO2);
        loyaltyTransactionDTO2.setId(loyaltyTransactionDTO1.getId());
        assertThat(loyaltyTransactionDTO1).isEqualTo(loyaltyTransactionDTO2);
        loyaltyTransactionDTO2.setId(2L);
        assertThat(loyaltyTransactionDTO1).isNotEqualTo(loyaltyTransactionDTO2);
        loyaltyTransactionDTO1.setId(null);
        assertThat(loyaltyTransactionDTO1).isNotEqualTo(loyaltyTransactionDTO2);
    }
}
