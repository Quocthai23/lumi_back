package com.lumiere.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AddressTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Address getAddressSample1() {
        return new Address().id(1L).fullName("fullName1").phone("phone1").street("street1").city("city1");
    }

    public static Address getAddressSample2() {
        return new Address().id(2L).fullName("fullName2").phone("phone2").street("street2").city("city2");
    }

    public static Address getAddressRandomSampleGenerator() {
        return new Address()
            .id(longCount.incrementAndGet())
            .fullName(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString())
            .street(UUID.randomUUID().toString())
            .city(UUID.randomUUID().toString());
    }
}
