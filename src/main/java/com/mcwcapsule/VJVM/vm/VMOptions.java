package com.mcwcapsule.VJVM.vm;

import lombok.Builder;
import lombok.Getter;
import lombok.val;

import java.util.regex.Pattern;

@Builder
@Getter
public class VMOptions {
    @Builder.Default
    private final String bootstrapClassPath = findJavaPath();
    @Builder.Default
    private final String userClassPath = ".";
    @Builder.Default
    private final int heapSize = 1024;
    private final String entryClass;
    private final String[] args;

    private static String findJavaPath() {
        val p = Pattern.compile("(\\d+\\.\\d+)\\.\\d+_\\d+");
        val m = p.matcher(System.getProperty("java.version"));
        assert m.find();
        assert Double.parseDouble(m.group(1)) <= 1.81;
        return System.getProperty("java.class.path");
    }
}
