package com.lumiere.app.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class FlashSaleProductTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static FlashSaleProduct getFlashSaleProductSample1() {
        return new FlashSaleProduct().id(1L).quantity(1).sold(1);
    }

    public static FlashSaleProduct getFlashSaleProductSample2() {
        return new FlashSaleProduct().id(2L).quantity(2).sold(2);
    }

    public static FlashSaleProduct getFlashSaleProductRandomSampleGenerator() {
        return new FlashSaleProduct().id(longCount.incrementAndGet()).quantity(intCount.incrementAndGet()).sold(intCount.incrementAndGet());
    }
}
