package com.lumiere.app.service.mapper;

import static com.lumiere.app.domain.ProductQuestionAsserts.*;
import static com.lumiere.app.domain.ProductQuestionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductQuestionMapperTest {

    private ProductQuestionMapper productQuestionMapper;

    @BeforeEach
    void setUp() {
        productQuestionMapper = new ProductQuestionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getProductQuestionSample1();
        var actual = productQuestionMapper.toEntity(productQuestionMapper.toDto(expected));
        assertProductQuestionAllPropertiesEquals(expected, actual);
    }
}
