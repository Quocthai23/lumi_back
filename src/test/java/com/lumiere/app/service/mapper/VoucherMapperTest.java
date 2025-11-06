package com.lumiere.app.service.mapper;

import static com.lumiere.app.domain.VoucherAsserts.*;
import static com.lumiere.app.domain.VoucherTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VoucherMapperTest {

    private VoucherMapper voucherMapper;

    @BeforeEach
    void setUp() {
        voucherMapper = new VoucherMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getVoucherSample1();
        var actual = voucherMapper.toEntity(voucherMapper.toDto(expected));
        assertVoucherAllPropertiesEquals(expected, actual);
    }
}
