package com.mcwcapsule.VJVM;

import lombok.val;

import java.nio.file.Files;
import java.nio.file.Path;

public class CompileUtil {
    public static Path compile(String... files) {
        try {
            val runtime = Runtime.getRuntime();
            Path tmp = Files.createTempDirectory(null);
            val cmd = String.format("javac -d %s src/test/java/testsource/%s", tmp,
                String.join(" src/test/java/testsource/", files));
            assert runtime.exec(cmd).waitFor() == 0;
            return tmp;
        } catch (Exception e) {
            throw new Error(e);
        }
    }
}
