package com.lumiere.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class StockMovementTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static StockMovement getStockMovementSample1() {
        return new StockMovement().id(1L).quantityChange(1L).note("note1");
    }

    public static StockMovement getStockMovementSample2() {
        return new StockMovement().id(2L).quantityChange(2L).note("note2");
    }

    public static StockMovement getStockMovementRandomSampleGenerator() {
        return new StockMovement()
            .id(longCount.incrementAndGet())
            .quantityChange(longCount.incrementAndGet())
            .note(UUID.randomUUID().toString());
    }
}
