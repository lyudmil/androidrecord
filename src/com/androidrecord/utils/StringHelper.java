package com.androidrecord.utils;

public class StringHelper {
    public static String pluralize(String string) {
        char lastCharacter = string.charAt(string.length() - 1);
        if (lastCharacter == 's') return string + "es";
        if (lastCharacter == 'y') {
            String start = string.substring(0, string.length() - 1);
            return start + "ies";
        }
        return string + "s";
    }

    public static String underscorize(String string) {
        String stripped = string.replaceAll(" ", "");
        String underscored = stripped.replaceAll("(.)([A-Z])", "$1_$2");
        return underscored.toLowerCase();
    }
}
