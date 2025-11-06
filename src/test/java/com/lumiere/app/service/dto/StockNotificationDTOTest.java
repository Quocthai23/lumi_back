package com.lumiere.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumiere.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StockNotificationDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(StockNotificationDTO.class);
        StockNotificationDTO stockNotificationDTO1 = new StockNotificationDTO();
        stockNotificationDTO1.setId(1L);
        StockNotificationDTO stockNotificationDTO2 = new StockNotificationDTO();
        assertThat(stockNotificationDTO1).isNotEqualTo(stockNotificationDTO2);
        stockNotificationDTO2.setId(stockNotificationDTO1.getId());
        assertThat(stockNotificationDTO1).isEqualTo(stockNotificationDTO2);
        stockNotificationDTO2.setId(2L);
        assertThat(stockNotificationDTO1).isNotEqualTo(stockNotificationDTO2);
        stockNotificationDTO1.setId(null);
        assertThat(stockNotificationDTO1).isNotEqualTo(stockNotificationDTO2);
    }
}
