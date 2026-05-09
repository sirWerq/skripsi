package com.warung.haryati.util;

import java.util.UUID;

public class IDGenerator {
    public static String generate(String prefix) {
        String randomPart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return (prefix != null ? prefix + "-" : "") + randomPart;
    }
}
