package com.mcwcapsule.VJVM;

import com.mcwcapsule.VJVM.classloader.searchpath.DirSearchPath;
import com.mcwcapsule.VJVM.classloader.searchpath.JarSearchPath;
import com.mcwcapsule.VJVM.classloader.searchpath.WildcardSearchPath;
import com.mcwcapsule.VJVM.utils.FileUtil;
import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ClassSearchPathTest {
    static Path base;

    @BeforeAll
    public static void compileAndPackFiles() {
        try {
            base = CompileUtil.compile("searchpath0/SearchTest0.java", "searchpath1/SearchTest1.java");
            base = base.resolve("testsource");
            // package
            val runtime = Runtime.getRuntime();
            assert runtime.exec(String.format(
                "jar -c -f %1$s/jar.jar -C %1$s searchpath0/SearchTest0.class -C %1$s searchpath1/SearchTest1.class",
                base.toString())).waitFor() == 0;
            // At this time, there are three files in base dir.
            // jar.jar
            // searchpath0/SearchTest0.class
            // searchpath1/SearchTest1.class
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    @Test
    public void testDir() {
        val _0 = new DirSearchPath(base.toString());
        assertNotEquals(null, _0.findClass("searchpath0/SearchTest0"));
        assertNotEquals(null, _0.findClass("searchpath1/SearchTest1"));
        val _1 = new DirSearchPath(base.resolve("searchpath0").toString());
        assertNotEquals(null, _1.findClass("SearchTest0"));
        assertNull(_1.findClass("SearchTest1"));
    }

    @Test
    public void testJar() {
        val _0 = new JarSearchPath(base.toString() + "/jar.jar");
        assertNotEquals(null, _0.findClass("searchpath0/SearchTest0"));
        assertNotEquals(null, _0.findClass("searchpath1/SearchTest1"));
    }

    @Test
    public void testWild() {
        val _0 = new WildcardSearchPath(base.toString() + "/*");
        assertNotEquals(null, _0.findClass("searchpath0/SearchTest0"));
        assertNotEquals(null, _0.findClass("searchpath1/SearchTest1"));
    }

    @AfterAll
    public static void cleanup() {
        System.gc();
        FileUtil.DeleteRecursive(base.resolve("../").toFile());
    }
}
