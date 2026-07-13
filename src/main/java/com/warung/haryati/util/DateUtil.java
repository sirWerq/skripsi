package com.warung.haryati.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateUtil {
    public static final Locale LOCALE_ID = new Locale("id", "ID");
    
    public static final DateTimeFormatter FORMAT_SHORT = DateTimeFormatter.ofPattern("dd-MM-yyyy", LOCALE_ID);
    public static final DateTimeFormatter FORMAT_LONG = DateTimeFormatter.ofPattern("dd MMMM yyyy", LOCALE_ID);

    public static String formatShort(LocalDate date) {
        if (date == null) return "-";
        return date.format(FORMAT_SHORT);
    }

    public static String formatShort(java.sql.Date sqlDate) {
        if (sqlDate == null) return "-";
        return sqlDate.toLocalDate().format(FORMAT_SHORT);
    }
    
    public static String formatShort(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return "-";
        try {
            LocalDate dt = LocalDate.parse(dateStr.trim());
            return dt.format(FORMAT_SHORT);
        } catch (Exception e) {
            return dateStr;
        }
    }

    public static String formatLong(LocalDate date) {
        if (date == null) return "-";
        return date.format(FORMAT_LONG);
    }

    public static String formatLong(java.sql.Date sqlDate) {
        if (sqlDate == null) return "-";
        return sqlDate.toLocalDate().format(FORMAT_LONG);
    }
}
