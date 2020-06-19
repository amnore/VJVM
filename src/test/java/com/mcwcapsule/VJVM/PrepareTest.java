package com.mcwcapsule.VJVM;

import java.nio.file.Files;
import java.nio.file.Path;

import com.mcwcapsule.VJVM.classloader.JClassLoader;
import com.mcwcapsule.VJVM.runtime.JClass;
import com.mcwcapsule.VJVM.runtime.metadata.FieldDescriptors;
import com.mcwcapsule.VJVM.utils.FileUtil;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.mcwcapsule.VJVM.runtime.metadata.FieldDescriptors.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import lombok.val;
import lombok.var;

public class PrepareTest {
    static JClass jClass;
    static Path path;
    static JClassLoader parent;
    static JClassLoader loader;

    @BeforeAll
    static void compileTest() {
        var runtime = Runtime.getRuntime();
        try {
            path = Files.createTempDirectory(null);
            assert runtime.exec(String.format("javac -d %s src/test/java/testsource/Test2.java", path.toString()))
                    .waitFor() == 0;
            parent = new JClassLoader(null, "lib");
            loader = new JClassLoader(parent, path.toString());
            jClass = loader.loadClass("testsource/Test2");
            jClass.prepare();
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    Object getField(String name, String descriptor) {
        val field = jClass.findField(name, descriptor);
        val slots = jClass.getStaticFields();
        switch (descriptor.charAt(0)) {
            case DESC_boolean:
            case DESC_byte:
            case DESC_char:
            case DESC_short:
            case DESC_int:
                return (Integer) slots.getInt(field.getOffset());
            case DESC_float:
                return (Float) slots.getFloat(field.getOffset());
            case DESC_long:
                return (Long) slots.getLong(field.getOffset());
            case DESC_double:
                return (Double) slots.getDouble(field.getOffset());
        }
        return null;
    }

    @Test
    void testInt() {
        assertEquals(0, getField("a", "I"));
    }

    @Test
    void testConstShort() {
        assertEquals(2, getField("b", "S"));
    }

    @Test
    void testConstDouble() {
        assertEquals(3.0, getField("c", "D"));
    }

    @Test
    void testLong() {
        assertEquals(0l, getField("d", "J"));
    }

    @AfterAll
    static void cleanup() {
        try {
            loader.close();
            parent.close();
            FileUtil.DeleteRecursive(path.toFile());
        } catch (Exception e) {
            throw new Error(e);
        }
    }
}
