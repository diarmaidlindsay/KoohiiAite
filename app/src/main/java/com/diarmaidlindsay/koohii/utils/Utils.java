package com.diarmaidlindsay.koohii.utils;

import java.util.List;

/**
 * Utility class for my application
 */
public class Utils {

    private Utils() {
    }

    public static int[] toIntArray(List<Integer> list)  {
        int[] ret = new int[list.size()];
        int i = 0;
        for (Integer e : list)
            ret[i++] = e;
        return ret;
    }

    public static boolean isNumeric(String value) {
        return value.matches("\\d+");
    }

    public static boolean isKanji(char value) {
        return Character.UnicodeBlock.of(value)
                == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS;
    }
}
