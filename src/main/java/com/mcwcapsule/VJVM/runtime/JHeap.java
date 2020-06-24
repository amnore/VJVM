package com.mcwcapsule.VJVM.runtime;

import lombok.Getter;

import java.util.ArrayList;

public class JHeap {
    private static final int extraSize = 2;
    private final ArrayList<JClass> methodArea;
    @Getter
    private final Slots slots;
    private int current = 0;

    public JHeap(int heapSize) {
        methodArea = new ArrayList<>();
        slots = new Slots(heapSize);
    }

    public int addJClass(JClass jClass) {
        methodArea.add(jClass);
        return methodArea.size() - 1;
    }

    public JClass getJClass(int index) {
        return methodArea.get(index);
    }

    /**
     * Allocates a piece of memory of specified size, and fill it with zero.
     */
    public int allocate(int size) {
        int ret = current + extraSize;
        // set object size
        slots.setInt(current, size);
        current += extraSize + size;
        return ret;
    }
}
