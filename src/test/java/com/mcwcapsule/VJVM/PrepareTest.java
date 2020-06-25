package com.mcwcapsule.VJVM;

import com.mcwcapsule.VJVM.classloader.JClassLoader;
import com.mcwcapsule.VJVM.runtime.JClass;
import com.mcwcapsule.VJVM.runtime.JHeap;
import com.mcwcapsule.VJVM.utils.ClassPathUtil;
import com.mcwcapsule.VJVM.utils.FileUtil;
import com.mcwcapsule.VJVM.vm.VJVM;
import lombok.val;
import lombok.var;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static com.mcwcapsule.VJVM.classfiledefs.FieldDescriptors.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrepareTest {
    static JClass jClass;
    static Path path;
    static JClassLoader parent;
    static JClassLoader loader;

    @BeforeAll
    static void compileTest() {
        var runtime = Runtime.getRuntime();
        try {
            // hack heap
            val heap = VJVM.class.getDeclaredField("heap");
            heap.setAccessible(true);
            heap.set(null, new JHeap(0));
            path = Files.createTempDirectory(null);
            assert runtime.exec(String.format("javac -d %s src/test/java/testsource/Test2.java", path.toString()))
                .waitFor() == 0;
            parent = new JClassLoader(null, ClassPathUtil.findJavaPath());
            loader = new JClassLoader(parent, path.toString());
            jClass = loader.loadClass("testsource/Test2");
            jClass.tryPrepare();
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
                return slots.getInt(field.getOffset());
            case DESC_float:
                return slots.getFloat(field.getOffset());
            case DESC_long:
                return slots.getLong(field.getOffset());
            case DESC_double:
                return slots.getDouble(field.getOffset());
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
        assertEquals(0L, getField("d", "J"));
    }

    @Test
    void testInstanceFields() {
        assertEquals(1, jClass.findField("e", "I").getSize());
        assertEquals(0, jClass.findField("e", "I").getOffset());
        assertEquals(1, jClass.findField("f", "F").getSize());
        assertEquals(1, jClass.findField("f", "F").getOffset());
        assertEquals(2, jClass.findField("g", "J").getSize());
        assertEquals(2, jClass.findField("g", "J").getOffset());
        assertEquals(2, jClass.findField("h", "D").getSize());
        assertEquals(4, jClass.findField("h", "D").getOffset());
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
