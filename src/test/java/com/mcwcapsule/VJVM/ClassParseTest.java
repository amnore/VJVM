package com.mcwcapsule.VJVM;

import static com.mcwcapsule.VJVM.runtime.metadata.ConstantTags.CONSTANT_Class;
import static com.mcwcapsule.VJVM.runtime.metadata.ConstantTags.CONSTANT_Double;
import static com.mcwcapsule.VJVM.runtime.metadata.ConstantTags.CONSTANT_Fieldref;
import static com.mcwcapsule.VJVM.runtime.metadata.ConstantTags.CONSTANT_Float;
import static com.mcwcapsule.VJVM.runtime.metadata.ConstantTags.CONSTANT_Integer;
import static com.mcwcapsule.VJVM.runtime.metadata.ConstantTags.CONSTANT_InterfaceMethodref;
import static com.mcwcapsule.VJVM.runtime.metadata.ConstantTags.CONSTANT_Long;
import static com.mcwcapsule.VJVM.runtime.metadata.ConstantTags.CONSTANT_Methodref;
import static com.mcwcapsule.VJVM.runtime.metadata.ConstantTags.CONSTANT_NameAndType;
import static com.mcwcapsule.VJVM.runtime.metadata.ConstantTags.CONSTANT_String;
import static com.mcwcapsule.VJVM.runtime.metadata.ConstantTags.CONSTANT_Utf8;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;

import com.mcwcapsule.VJVM.runtime.JClass;
import com.mcwcapsule.VJVM.runtime.JHeap;
import com.mcwcapsule.VJVM.runtime.NonArrayClass;
import com.mcwcapsule.VJVM.runtime.metadata.constant.ClassRef;
import com.mcwcapsule.VJVM.runtime.metadata.constant.NameAndTypeConstant;
import com.mcwcapsule.VJVM.runtime.metadata.constant.StringConstant;
import com.mcwcapsule.VJVM.utils.FileUtil;
import com.mcwcapsule.VJVM.vm.VJVM;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.ConstantCP;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantNameAndType;
import org.apache.bcel.classfile.ConstantString;
import org.apache.bcel.classfile.JavaClass;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import lombok.Cleanup;
import lombok.val;
import lombok.var;

public class ClassParseTest {
    static Path classPath;
    static JavaClass realClass;
    static JClass myClass;

    @BeforeAll
    public static void loadTestClass() {
        var runtime = Runtime.getRuntime();
        try {
            // hack heap
            val heap = VJVM.class.getDeclaredField("heap");
            heap.setAccessible(true);
            heap.set(null, new JHeap(0));
            classPath = Files.createTempDirectory("testtemp");
            var cmp = runtime.exec(String.format("javac -d %s src/test/java/testsource/Test1.java", classPath));
            cmp.waitFor();
            classPath = classPath.resolve("testsource/Test1.class");
            realClass = new ClassParser(classPath.toString()).parse();
            @Cleanup
            var raFile = new RandomAccessFile(classPath.toFile(), "r");
            myClass = new NonArrayClass(raFile, null);
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
                        assertEquals(((ConstantClass) realConstant).getBytes(realPool),
                                ((ClassRef) myConstant).getName());
                        break;
                    case CONSTANT_Fieldref:
                    case CONSTANT_Methodref:
                    case CONSTANT_InterfaceMethodref:
                        var _1 = (ConstantCP) realConstant;
                        var _2 = myConstant.getClass();
                        var _f1 = _2.getDeclaredMethod("getClassRef");
                        var _f2 = _2.getDeclaredMethod("getName");
                        var _f3 = _2.getDeclaredMethod("getDescriptor");
                        assertEquals(_1.getClass(realPool).replace('.', '/'),
                                ((ClassRef) _f1.invoke(myConstant)).getName());
                        var _n = (ConstantNameAndType) realPool.getConstant(_1.getNameAndTypeIndex());
                        assertEquals(_n.getName(realPool).replace('.', '/'), _f2.invoke(myConstant));
                        assertEquals(_n.getSignature(realPool).replace('.', '/'), _f3.invoke(myConstant));
                        break;
                    case CONSTANT_String:
                        assertEquals(((ConstantString) realConstant).getBytes(realPool),
                                ((StringConstant) myConstant).getValue());
                        break;
                    case CONSTANT_NameAndType:
                        var _3 = (ConstantNameAndType) realConstant;
                        assertEquals(_3.getName(realPool), ((NameAndTypeConstant) myConstant).getName());
                        assertEquals(_3.getSignature(realPool), ((NameAndTypeConstant) myConstant).getDescriptor());
                        break;
                    case CONSTANT_Integer:
                    case CONSTANT_Float:
                    case CONSTANT_Long:
                    case CONSTANT_Double:
                    case CONSTANT_Utf8:
                        assertEquals(realConstant.getClass().getMethod("getBytes").invoke(realConstant),
                                myConstant.getClass().getMethod("getValue").invoke(myConstant));
                        break;
                }
            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }

    @Test
    public void testAccessFlags() {
        assertEquals(realClass.getAccessFlags(), myClass.getAccessFlags());
    }

    @AfterAll
    public static void cleanUp() {
        FileUtil.DeleteRecursive(classPath.toFile());
    }
}
