package com.mcwcapsule.VJVM.utils;

import lombok.val;

import java.util.regex.Pattern;

public class ClassPathUtil {
    public static String findJavaPath() {
        val p = Pattern.compile("(\\d+\\.\\d+)\\.\\d+_\\d+");
        val m = p.matcher(System.getProperty("java.version"));
        assert m.find();
        assert Double.parseDouble(m.group(1)) <= 1.81;
        return System.getProperty("java.class.path");
    }
}
