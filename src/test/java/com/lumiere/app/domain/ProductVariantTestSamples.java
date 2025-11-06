package com.lumiere.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ProductVariantTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ProductVariant getProductVariantSample1() {
        return new ProductVariant().id(1L).sku("sku1").name("name1").currency("currency1").stockQuantity(1L).color("color1").size("size1");
    }

    public static ProductVariant getProductVariantSample2() {
        return new ProductVariant().id(2L).sku("sku2").name("name2").currency("currency2").stockQuantity(2L).color("color2").size("size2");
    }

    public static ProductVariant getProductVariantRandomSampleGenerator() {
        return new ProductVariant()
            .id(longCount.incrementAndGet())
            .sku(UUID.randomUUID().toString())
            .name(UUID.randomUUID().toString())
            .currency(UUID.randomUUID().toString())
            .stockQuantity(longCount.incrementAndGet())
            .color(UUID.randomUUID().toString())
            .size(UUID.randomUUID().toString());
    }
}
