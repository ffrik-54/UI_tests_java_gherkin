package com.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateParser {

    private static final String defaultDatePattern = "yyyy/MM/dd HH:mm:ss";

    // date pattern used locally in EUROPE (FR) for the dates in the BO reports
    public static final String DATE_PATTERN_LOCAL = "dd/MM/yyyy HH:mm";

    // date pattern used by SAUCELABS for the dates in the BO reports
    public static final String DATE_PATTERN_SAUCELABS = "M/d/yyyy, h:mm";

    // date pattern used by DateTime format ISO.
    public static final String DATE_TIME_PATTERN_ISO = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    // date pattern used by the 'display' element in a raw-ticket.
    public static final String DATE_TIME_PATTERN_RAW = "yyyy-MM-dd'T'HH:mm:ss";

    // date pattern used by the 'raw' element in a raw-ticket.
    public static final String DATE_TIME_PATTERN_DISPLAY = "M/d/yyyy, h:mm:ss a";

    public static String getCurrentDate() {
        return getCurrentDate(defaultDatePattern);
    }

    public static String getCurrentDate(String pattern) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }
}
