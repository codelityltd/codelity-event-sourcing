package uk.co.codelity.event.sourcing.core.utils;

import java.util.Arrays;

public class StringUtils {
    private StringUtils() {
    }

    public static String merge(String[] strings, String delimiter) {
        StringBuilder sb = new StringBuilder();
        Arrays.stream(strings)
                .forEach(s -> {
                    if(sb.length() > 0) {
                        sb.append(delimiter);
                    }

                    sb.append(s);
                });
        return sb.toString();
    }
}
