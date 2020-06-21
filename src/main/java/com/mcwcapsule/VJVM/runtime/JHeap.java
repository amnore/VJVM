package com.mcwcapsule.VJVM.runtime;

import java.util.ArrayList;

public class JHeap {
    private ArrayList<JClass> methodArea;
    private Slots slots;
    private int current = 0;

    private static final int extraSize = 1;

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

    public int allocate(int size) {
        int ret = current + extraSize;
        current += extraSize + size;
        return ret;
    }
}
