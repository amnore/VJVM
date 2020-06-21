package com.mcwcapsule.VJVM.vm;

import java.util.ArrayList;

import com.mcwcapsule.VJVM.interpreter.JInterpreter;
import com.mcwcapsule.VJVM.runtime.JHeap;
import com.mcwcapsule.VJVM.runtime.JThread;

import lombok.Getter;

public class VJVM {
    @Getter
    private static JHeap heap;
    @Getter
    private static JInterpreter interpreter;
    private static ArrayList<JThread> threads = new ArrayList<>();
    @Getter
    private static VMOptions options;

    public static void addThread(JThread thread) {
        threads.add(thread);
    }

    public static void removeThread(JThread thread) {
        threads.remove(thread);
    }

    public static void init(VMOptions options) {
        // TODO: init
    }
}
