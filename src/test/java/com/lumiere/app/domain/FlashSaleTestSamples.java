package com.lumiere.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class FlashSaleTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static FlashSale getFlashSaleSample1() {
        return new FlashSale().id(1L).name("name1");
    }

    public static FlashSale getFlashSaleSample2() {
        return new FlashSale().id(2L).name("name2");
    }

    public static FlashSale getFlashSaleRandomSampleGenerator() {
        return new FlashSale().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString());
    }
}
