package com.utils;

import java.text.Normalizer;


/**
 * Methods for Rest request
 *
 * @author ffrik
 */

public class StringUtils {


    public static String stripAccents(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return s;
    }

    public static String capitalizeFirstLetterOfString(String str) {
        str = str.toLowerCase();
        str = str.substring(0, 1).toUpperCase() + str.substring(1);
        return str;
    }

    /**
     * Check if a String is Null or Empty
     *
     * @param str (String) to check.
     * @return true if the String null or Empty
     */
    public static boolean isNullOrEmpty(String str) {
        return (str == null || str.isEmpty());
    }
}