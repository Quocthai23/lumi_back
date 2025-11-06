package com.lumiere.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class StockNotificationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static StockNotification getStockNotificationSample1() {
        return new StockNotification().id(1L).email("email1");
    }

    public static StockNotification getStockNotificationSample2() {
        return new StockNotification().id(2L).email("email2");
    }

    public static StockNotification getStockNotificationRandomSampleGenerator() {
        return new StockNotification().id(longCount.incrementAndGet()).email(UUID.randomUUID().toString());
    }
}
