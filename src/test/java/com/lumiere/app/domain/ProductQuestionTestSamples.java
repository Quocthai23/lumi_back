package com.lumiere.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ProductQuestionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ProductQuestion getProductQuestionSample1() {
        return new ProductQuestion().id(1L).author("author1");
    }

    public static ProductQuestion getProductQuestionSample2() {
        return new ProductQuestion().id(2L).author("author2");
    }

    public static ProductQuestion getProductQuestionRandomSampleGenerator() {
        return new ProductQuestion().id(longCount.incrementAndGet()).author(UUID.randomUUID().toString());
    }
}
