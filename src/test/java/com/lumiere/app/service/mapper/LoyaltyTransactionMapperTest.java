package com.lumiere.app.service.mapper;

import static com.lumiere.app.domain.LoyaltyTransactionAsserts.*;
import static com.lumiere.app.domain.LoyaltyTransactionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LoyaltyTransactionMapperTest {

    private LoyaltyTransactionMapper loyaltyTransactionMapper;

    @BeforeEach
    void setUp() {
        loyaltyTransactionMapper = new LoyaltyTransactionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getLoyaltyTransactionSample1();
        var actual = loyaltyTransactionMapper.toEntity(loyaltyTransactionMapper.toDto(expected));
        assertLoyaltyTransactionAllPropertiesEquals(expected, actual);
    }
}
