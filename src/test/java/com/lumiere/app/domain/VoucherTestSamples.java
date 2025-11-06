package com.lumiere.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class VoucherTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Voucher getVoucherSample1() {
        return new Voucher().id(1L).code("code1").usageLimit(1).usageCount(1);
    }

    public static Voucher getVoucherSample2() {
        return new Voucher().id(2L).code("code2").usageLimit(2).usageCount(2);
    }

    public static Voucher getVoucherRandomSampleGenerator() {
        return new Voucher()
            .id(longCount.incrementAndGet())
            .code(UUID.randomUUID().toString())
            .usageLimit(intCount.incrementAndGet())
            .usageCount(intCount.incrementAndGet());
    }
}
