package com.lumiere.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ProductTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Product getProductSample1() {
        return new Product().id(1L).code("code1").name("name1").slug("slug1").category("category1").material("material1").reviewCount(1);
    }

    public static Product getProductSample2() {
        return new Product().id(2L).code("code2").name("name2").slug("slug2").category("category2").material("material2").reviewCount(2);
    }

    public static Product getProductRandomSampleGenerator() {
        return new Product()
            .id(longCount.incrementAndGet())
            .code(UUID.randomUUID().toString())
            .name(UUID.randomUUID().toString())
            .slug(UUID.randomUUID().toString())
            .category(UUID.randomUUID().toString())
            .material(UUID.randomUUID().toString())
            .reviewCount(intCount.incrementAndGet());
    }
}
