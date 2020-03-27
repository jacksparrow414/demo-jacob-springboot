package com.example.jacob.springboot.demo.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 验证url是否合法工具类
 * @author duhongbo
 * @date 2020/3/20 10:23
 */
public class VerifyUrlUtil {

    public static boolean isHttpUrl(String url) {
        String regex = "^(http|https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        Pattern pat = Pattern.compile(regex.trim());
        Matcher mat = pat.matcher(url.trim());
         return mat.matches();
    }
}
