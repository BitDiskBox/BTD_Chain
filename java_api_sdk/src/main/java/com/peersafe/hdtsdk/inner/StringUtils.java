package com.peersafe.hdtsdk.inner;

import com.peersafe.hdtsdk.BuildConfig;
import java.util.regex.Pattern;

public class StringUtils {
    private static final String TAG = StringUtils.class.getSimpleName();

    public static boolean isEmpty(String input) {
        if (input == null || BuildConfig.FLAVOR.equals(input)) {
            return true;
        }
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != 9 && c != 13 && c != 10) {
                return false;
            }
        }
        return true;
    }

    public static String nullToEmpty(String input) {
        return isEmpty(input) ? BuildConfig.FLAVOR : input;
    }

    public static boolean isNumeric(String str) {
        if (!Pattern.compile("[0-9]*").matcher(str).matches()) {
            return false;
        }
        return true;
    }

    public static boolean isDouble(String str) {
        return Pattern.compile("[0-9]{0,99}\\.{0,1}[0-9]{0,2}").matcher(str).matches();
    }

    public static String trimTailBlank(String str) {
        if (isEmpty(str)) {
            return BuildConfig.FLAVOR;
        }
        return ("A" + str).trim().substring(1);
    }

    public static String ByteArrayToStr(byte[] byteArray) throws Exception {
        return new String(byteArray, "UTF-8");
    }

    public static String ByteToStr(byte[] byteArray) {
        String value = BuildConfig.FLAVOR;
        try {
            return ByteArrayToStr(byteArray);
        } catch (Exception e) {
            return value;
        }
    }

    public static byte[] StrToByteArray(String str) {
        try {
            return str.getBytes("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String[] toStringArray(Object[] values) {
        int length = values.length;
        String[] strings = new String[length];
        for (int i = 0; i < length; i++) {
            Object object = values[i];
            if (object != null) {
                strings[i] = object.toString();
            } else {
                strings[i] = null;
            }
        }
        return strings;
    }

    public static boolean isLetterDigit(String str) {
        boolean isDigit = false;
        boolean isLetter = false;
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i))) {
                isDigit = true;
            }
            if (Character.isLetter(str.charAt(i))) {
                isLetter = true;
            }
        }
        return isDigit && isLetter && str.matches("^[a-zA-Z0-9]+$");
    }

    public static boolean isLetter(String str) {
        return Pattern.compile("[a-zA-Z]*").matcher(str).matches();
    }

    public static String getHeadTailString(String str) {
        return getHeadTailString(str, 4, 4);
    }

    public static String refactorLotteryName(String lotteryName) {
        return "\"" + getHeadTailString(lotteryName) + "\"";
    }

    public static String getHeadTailString(String str, int head, int tail) {
        if (isEmpty(str) || head <= 0 || tail <= 0 || head + tail >= str.length()) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str.substring(0, head));
        sb.append("...");
        sb.append(str.substring(str.length() - tail));
        return sb.toString();
    }

    public static String str2HexStr(String str) {
        byte[] bs;
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder(BuildConfig.FLAVOR);
        for (byte b : str.getBytes()) {
            sb.append(chars[(b & 240) >> 4]);
            sb.append(chars[b & 15]);
        }
        return sb.toString().trim();
    }

    public static String hexString2String(String src) {
        String temp = BuildConfig.FLAVOR;
        for (int i = 0; i < src.length() / 2; i++) {
            temp = temp + ((char) Integer.valueOf(src.substring(i * 2, (i * 2) + 2), 16).byteValue());
        }
        return temp;
    }
}
