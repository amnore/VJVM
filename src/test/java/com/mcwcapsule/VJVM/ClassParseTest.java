package com.mcwcapsule.VJVM;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static com.mcwcapsule.VJVM.runtime.metadata.ConstantTags.*;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.jar.Attributes.Name;

import javax.management.RuntimeErrorException;

import com.mcwcapsule.VJVM.runtime.JClass;
import com.mcwcapsule.VJVM.runtime.metadata.constant.*;

import org.apache.bcel.classfile.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import lombok.Cleanup;
import lombok.var;

public class ClassParseTest {
    static Path classPath;
    static JavaClass realClass;
    static JClass myClass;

    @BeforeAll
    public static void loadTestClass() {
        var runtime = Runtime.getRuntime();
        try {
            classPath = Files.createTempDirectory("testtemp");
            var cmp = runtime.exec(String.format("javac -d %s src/test/java/testsource/Test1.java", classPath));
            cmp.waitFor();
            classPath = classPath.resolve("testsource/Test1.class");
            realClass = new ClassParser(classPath.toString()).parse();
            @Cleanup
            var raFile = new RandomAccessFile(classPath.toFile(), "r");
            myClass = new JClass(raFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testVersion() {
        assertEquals(realClass.getMinor(), myClass.getMinorVersion());
        assertEquals(realClass.getMajor(), myClass.getMajorVersion());
    }

    @Test
    public void testConstantPool() {
        var myPool = myClass.getConstantPool();
        var realPool = realClass.getConstantPool();
        for (int i = 1; i < myPool.size(); ++i) {
            var myConstant = myPool.getConstant(i);
            var realConstant = realPool.getConstant(i);
            try {
                switch (realConstant.getTag()) {
                    case CONSTANT_Class:
                        var _f0 = ClassRef.class.getDeclaredField("nameIndex");
                        _f0.setAccessible(true);
                        assertEquals(((ConstantClass) realConstant).getNameIndex(), _f0.get(myConstant));
                        break;
                    case CONSTANT_Fieldref:
                    case CONSTANT_Methodref:
                    case CONSTANT_InterfaceMethodref:
                        var _1 = (ConstantCP) realConstant;
                        var _2 = myConstant.getClass();
                        var _f1 = _2.getDeclaredField("classIndex");
                        _f1.setAccessible(true);
                        var _f2 = _2.getDeclaredField("nameAndTypeIndex");
                        _f2.setAccessible(true);
                        assertEquals(_1.getClassIndex(), _f1.get(myConstant));
                        assertEquals(_1.getNameAndTypeIndex(), _f2.get(myConstant));
                        break;
                    case CONSTANT_String:
                        var _f3 = StringConstant.class.getDeclaredField("stringIndex");
                        _f3.setAccessible(true);
                        assertEquals(((ConstantString) realConstant).getStringIndex(), _f3.get(myConstant));
                        break;
                    case CONSTANT_NameAndType:
                        var _3 = (ConstantNameAndType) realConstant;
                        _2 = myConstant.getClass();
                        _f1 = _2.getDeclaredField("nameIndex");
                        _f1.setAccessible(true);
                        _f2 = _2.getDeclaredField("descriptorIndex");
                        _f2.setAccessible(true);
                        assertEquals(_3.getNameIndex(), _f1.get(myConstant));
                        assertEquals(_3.getSignatureIndex(), _f2.get(myConstant));
                        break;
                    case CONSTANT_Integer:
                    case CONSTANT_Float:
                    case CONSTANT_Long:
                    case CONSTANT_Double:
                    case CONSTANT_Utf8:
                        assertEquals(realConstant.getClass().getMethod("getBytes").invoke(realConstant),
                                ((ValueConstant) myConstant).getValue());
                        break;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    public void testAccessFlags() {
        assertEquals(realClass.getAccessFlags(), myClass.getAccessFlags());
    }

    @AfterAll
    public static void cleanUp() {
        try {
            Files.deleteIfExists(classPath);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }
}
