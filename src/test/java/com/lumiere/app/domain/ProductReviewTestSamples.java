package com.lumiere.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ProductReviewTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ProductReview getProductReviewSample1() {
        return new ProductReview().id(1L).author("author1");
    }

    public static ProductReview getProductReviewSample2() {
        return new ProductReview().id(2L).author("author2");
    }

    public static ProductReview getProductReviewRandomSampleGenerator() {
        return new ProductReview().id(longCount.incrementAndGet()).author(UUID.randomUUID().toString());
    }
}
