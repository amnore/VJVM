package com.mcwcapsule.VJVM;

import com.mcwcapsule.VJVM.classloader.JClassLoader;
import com.mcwcapsule.VJVM.runtime.JHeap;
import com.mcwcapsule.VJVM.utils.FileUtil;
import com.mcwcapsule.VJVM.vm.VJVM;
import lombok.val;
import lombok.var;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClassLoaderTest {
    static Path classPath;
    static JClassLoader parent;
    static JClassLoader loader;

    @BeforeAll
    public static void loadTestClass() {
        var runtime = Runtime.getRuntime();
        try {
            // hack heap
            val heap = VJVM.class.getDeclaredField("heap");
            heap.setAccessible(true);
            heap.set(null, new JHeap(0));
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
        assertEquals(parent.getDefinedClass("java/lang/Object"),
                jClass.getSuperClass().getJClass().getSuperClass().getJClass());
        assertEquals(loader.getDefinedClass("testsource/Test2"), jClass.getSuperClass().getJClass());
        assertEquals(loader.getDefinedClass("testsource/Test3"), jClass.getSuperInterface(0).getJClass());
    }

    @AfterAll
    public static void cleanup() {
        try {
            parent.close();
            loader.close();
            FileUtil.DeleteRecursive(classPath.toFile());
        } catch (IOException e) {
            throw new Error(e);
        }
    }
}
