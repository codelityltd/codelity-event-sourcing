package uk.co.codelity.event.sourcing.core.utils;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EventHandlerCode {
    private EventHandlerCode() {
    }

    public static String generate(Method method) throws NoSuchAlgorithmException {
        String subscriber = method.getDeclaringClass().getName() + "::" + method.getName();
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] bytes = md.digest(subscriber.getBytes(StandardCharsets.UTF_8));
        return encodeHexString(bytes);
    }

    private static String encodeHexString(byte[] byteArray) {
        StringBuilder hexStringBuffer = new StringBuilder();
        for (int i = 0; i < byteArray.length; i++) {
            hexStringBuffer.append(byteToHex(byteArray[i]));
        }
        return hexStringBuffer.toString();
    }

    private static String byteToHex(byte num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits);
    }
}
