package com.mcwcapsule.VJVM;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.mcwcapsule.VJVM.classloader.JClassLoader;
import com.mcwcapsule.VJVM.utils.FileUtil;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import lombok.val;
import lombok.var;

public class ClassLoaderTest {
    static Path classPath;
    static JClassLoader parent;
    static JClassLoader loader;

    @BeforeAll
    public static void loadTestClass() {
        var runtime = Runtime.getRuntime();
        try {
            classPath = Files.createTempDirectory(null);
            runtime.exec(String.format("javac -d %s %2$s/Test2.java %2$s/Test3.java %2$s/Test4.java", classPath,
                    "src/test/java/testsource")).waitFor();
            parent = new JClassLoader(null, "lib");
            loader = new JClassLoader(parent, classPath.toString());
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    @Test
    void testParent() throws Exception {
        assertEquals(parent, loader.loadClass("java/lang/Object").getClassLoader());
    }

    @Test
    void testRecursive() throws Exception {
        val jClass = loader.loadClass("testsource/Test4");
        assertEquals(parent.getDefinedClass("testsource/Test2"), jClass.getSuperClass().getJClass());
        assertEquals(loader.getDefinedClass("testsource/Test3"), jClass.getSuperInterface(0));
    }

    @AfterAll
    public static void cleanup() {
        FileUtil.DeleteRecursive(classPath.toFile());
        try {
            parent.close();
            loader.close();
        } catch (IOException e) {
            throw new Error(e);
        }
    }
}