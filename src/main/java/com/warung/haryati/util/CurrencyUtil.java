package com.warung.haryati.util;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyUtil {
    private static final NumberFormat nf = NumberFormat.getInstance(new Locale("id", "ID"));

    public static String format(double amount) {
        return "Rp " + nf.format(amount);
    }
}
