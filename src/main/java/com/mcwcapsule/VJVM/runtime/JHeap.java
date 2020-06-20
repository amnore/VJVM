package com.mcwcapsule.VJVM.runtime;

import java.util.ArrayList;

import lombok.Getter;

public class JHeap {
    // extra size to allocate
    private static final int extraSize = 1;

    private static final int defaultHeapSize = 1024;

    private static JHeap instance = new JHeap(defaultHeapSize);

    private ArrayList<JClass> methodArea;
    @Getter
    private Slots slots;
    private int current;

    private JHeap(int heapSize) {
        methodArea = new ArrayList<>();
        slots = new Slots(heapSize);
        current = 0;
    }

    public int addJClass(JClass jClass) {
        methodArea.add(jClass);
        return methodArea.size() - 1;
    }

    public JClass getJClass(int index) {
        return methodArea.get(index);
    }

    /**
     * Allocate an object and return its base index.
     */
    public int allocate(int size) {
        int ret = current + extraSize;
        // simply increase the current pointer
        current += extraSize + size;
        return ret;
    }

    public static JHeap getInstance() {
        return instance;
    }
}
