package com.lumiere.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class LoyaltyTransactionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static LoyaltyTransaction getLoyaltyTransactionSample1() {
        return new LoyaltyTransaction().id(1L).points(1).description("description1");
    }

    public static LoyaltyTransaction getLoyaltyTransactionSample2() {
        return new LoyaltyTransaction().id(2L).points(2).description("description2");
    }

    public static LoyaltyTransaction getLoyaltyTransactionRandomSampleGenerator() {
        return new LoyaltyTransaction()
            .id(longCount.incrementAndGet())
            .points(intCount.incrementAndGet())
            .description(UUID.randomUUID().toString());
    }
}
