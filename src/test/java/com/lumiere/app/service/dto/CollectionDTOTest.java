package com.lumiere.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.lumiere.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CollectionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CollectionDTO.class);
        CollectionDTO collectionDTO1 = new CollectionDTO();
        collectionDTO1.setId(1L);
        CollectionDTO collectionDTO2 = new CollectionDTO();
        assertThat(collectionDTO1).isNotEqualTo(collectionDTO2);
        collectionDTO2.setId(collectionDTO1.getId());
        assertThat(collectionDTO1).isEqualTo(collectionDTO2);
        collectionDTO2.setId(2L);
        assertThat(collectionDTO1).isNotEqualTo(collectionDTO2);
        collectionDTO1.setId(null);
        assertThat(collectionDTO1).isNotEqualTo(collectionDTO2);
    }
}
