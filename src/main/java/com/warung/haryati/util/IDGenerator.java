package com.warung.haryati.util;

import java.util.concurrent.atomic.AtomicInteger;

public class IDGenerator {
    private static final AtomicInteger counter = new AtomicInteger(0);

    public static String generate(String prefix) {
        // Keep the 'T' for chronological sorting after old UUIDs, but add a unique counter
        long time = System.currentTimeMillis();
        int count = counter.incrementAndGet() % 10000;
        String uniqueSuffix = String.format("%04d", count);
        return (prefix != null ? prefix + "-T" : "T") + time + uniqueSuffix;
    }
}
