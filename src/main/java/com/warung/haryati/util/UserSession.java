package com.warung.haryati.util;

public class UserSession {
    private static String idPemilik;

    public static String getIdPemilik() {
        return idPemilik;
    }

    public static void setIdPemilik(String id) {
        idPemilik = id;
    }

    public static void clear() {
        idPemilik = null;
    }
}
