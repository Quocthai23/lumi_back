package com.lumiere.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class OrdersTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Orders getOrdersSample1() {
        return new Orders().id(1L).code("code1").note("note1").paymentMethod("paymentMethod1").redeemedPoints(1);
    }

    public static Orders getOrdersSample2() {
        return new Orders().id(2L).code("code2").note("note2").paymentMethod("paymentMethod2").redeemedPoints(2);
    }

    public static Orders getOrdersRandomSampleGenerator() {
        return new Orders()
            .id(longCount.incrementAndGet())
            .code(UUID.randomUUID().toString())
            .note(UUID.randomUUID().toString())
            .paymentMethod(UUID.randomUUID().toString())
            .redeemedPoints(intCount.incrementAndGet());
    }
}
