package com.mcwcapsule.VJVM.vm;

import java.util.ArrayList;

import com.mcwcapsule.VJVM.classloader.JClassLoader;
import com.mcwcapsule.VJVM.interpreter.JInterpreter;
import com.mcwcapsule.VJVM.runtime.JHeap;
import com.mcwcapsule.VJVM.runtime.JThread;

import lombok.Getter;
import lombok.val;

public class VJVM {
    @Getter
    private static JHeap heap;
    @Getter
    private static JInterpreter interpreter;
    private static ArrayList<JThread> threads = new ArrayList<>();
    @Getter
    private static VMOptions options;
    @Getter
    static JClassLoader bootstrapLoader;
    @Getter
    static JClassLoader userLoader;

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
        try {
            val initClass = userLoader.loadClass(_options.getEntryClass());
            initClass.verify();
            initClass.prepare();
            initClass.initialize(initThread);
            // TODO: call main
        } catch (Exception e) {
            throw new Error(e);
        }
    }
}
