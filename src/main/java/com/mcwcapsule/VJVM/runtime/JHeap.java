package com.mcwcapsule.VJVM.runtime;

import java.util.ArrayList;

import lombok.Getter;

public class JHeap {
    private ArrayList<JClass> methodArea;
    @Getter
    private Slots slots;
    private int current = 0;

    private static final int extraSize = 2;

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

        current += extraSize + size;
        // set object size
        slots.setInt(current - 2, size);
        return ret;
    }
}
