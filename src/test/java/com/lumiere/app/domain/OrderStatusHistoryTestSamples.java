package com.lumiere.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class OrderStatusHistoryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static OrderStatusHistory getOrderStatusHistorySample1() {
        return new OrderStatusHistory().id(1L).description("description1");
    }

    public static OrderStatusHistory getOrderStatusHistorySample2() {
        return new OrderStatusHistory().id(2L).description("description2");
    }

    public static OrderStatusHistory getOrderStatusHistoryRandomSampleGenerator() {
        return new OrderStatusHistory().id(longCount.incrementAndGet()).description(UUID.randomUUID().toString());
    }
}
