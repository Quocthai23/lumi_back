package com.lumiere.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CollectionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Collection getCollectionSample1() {
        return new Collection().id(1L).name("name1").slug("slug1").imageUrl("imageUrl1").lookImageUrl("lookImageUrl1");
    }

    public static Collection getCollectionSample2() {
        return new Collection().id(2L).name("name2").slug("slug2").imageUrl("imageUrl2").lookImageUrl("lookImageUrl2");
    }

    public static Collection getCollectionRandomSampleGenerator() {
        return new Collection()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .slug(UUID.randomUUID().toString())
            .imageUrl(UUID.randomUUID().toString())
            .lookImageUrl(UUID.randomUUID().toString());
    }
}
