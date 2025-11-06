package com.lumiere.app.service.mapper;

import static com.lumiere.app.domain.CollectionAsserts.*;
import static com.lumiere.app.domain.CollectionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CollectionMapperTest {

    private CollectionMapper collectionMapper;

    @BeforeEach
    void setUp() {
        collectionMapper = new CollectionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCollectionSample1();
        var actual = collectionMapper.toEntity(collectionMapper.toDto(expected));
        assertCollectionAllPropertiesEquals(expected, actual);
    }
}
