package com.lumiere.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ProductAnswerTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ProductAnswer getProductAnswerSample1() {
        return new ProductAnswer().id(1L).author("author1");
    }

    public static ProductAnswer getProductAnswerSample2() {
        return new ProductAnswer().id(2L).author("author2");
    }

    public static ProductAnswer getProductAnswerRandomSampleGenerator() {
        return new ProductAnswer().id(longCount.incrementAndGet()).author(UUID.randomUUID().toString());
    }
}
