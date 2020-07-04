package com.mcwcapsule.VJVM.vm;

import com.mcwcapsule.VJVM.classloader.JClassLoader;
import com.mcwcapsule.VJVM.interpreter.JInterpreter;
import com.mcwcapsule.VJVM.runtime.JHeap;
import com.mcwcapsule.VJVM.runtime.JThread;
import com.mcwcapsule.VJVM.runtime.Slots;
import com.mcwcapsule.VJVM.utils.CallUtil;
import com.mcwcapsule.VJVM.utils.TestUtilException;
import lombok.Getter;
import lombok.val;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

public class VJVM {
    private static final ArrayList<JThread> threads = new ArrayList<>();
    @Getter
    static JClassLoader bootstrapLoader;
    @Getter
    static JClassLoader userLoader;
    @Getter
    private static JHeap heap;
    @Getter
    private static JInterpreter interpreter;
    @Getter
    private static VMOptions options;

    public static void addThread(JThread thread) {
        threads.add(thread);
    }

    public static void removeThread(JThread thread) {
        threads.remove(thread);
    }

    public static void init(VMOptions _options) {
        interpreter = new JInterpreter();
        heap = new JHeap(_options.getHeapSize());
        options = _options;
        bootstrapLoader = new JClassLoader(null, _options.getBootstrapClassPath());
        userLoader = new JClassLoader(bootstrapLoader, _options.getUserClassPath());
        val initThread = new JThread();
        addThread(initThread);

        // set err stream
        val myErr = new ByteArrayOutputStream();
        System.setErr(new PrintStream(myErr));
        val oldOut = System.out;
        val myOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(myOut));
        try {

            val initClass = userLoader.loadClass(_options.getEntryClass().replace('.', '/'));
            initClass.tryInitialize(initThread);

            // hack: string
            val strClass = bootstrapLoader.loadClass("java/lang/String");
            strClass.tryInitialize(initThread);

            val mainMethod = initClass.findMethod("main", "([Ljava/lang/String;)V");
            assert mainMethod.getJClass() == initClass;
            // FIXME: call main with arguments
            CallUtil.callMethodWithArgs(mainMethod, initThread, new Slots(1));
            interpreter.run(initThread);
        } catch (TestUtilException e) {
            throw e;
        } catch (Exception e) {
            // print interpreter trace
            throw new Error(e);
        } finally {
            System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err)));
            System.err.println(myErr.toString());
            System.err.println("Output: ");
            System.err.println(myOut.toString());
            oldOut.print(myOut.toString());
        }
    }
}
