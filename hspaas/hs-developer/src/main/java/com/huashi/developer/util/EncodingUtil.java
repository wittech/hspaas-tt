package com.huashi.developer.util;

import org.apache.commons.lang3.StringUtils;

public class EncodingUtil {

    public static boolean isUtf8(String text) {
        if (StringUtils.isEmpty(text)) {
            return false;
        }

        // text.eq
        // if(text.equals(new String(text.getBytes("iso8859-1"), "iso8859-1")))
        // {
        // 　　destination=new String(destination.getBytes("iso8859-1"),"utf-8");
        // }

        return true;
    }
}
