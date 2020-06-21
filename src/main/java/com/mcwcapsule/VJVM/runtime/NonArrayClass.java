package com.mcwcapsule.VJVM.runtime;

import java.io.DataInput;

import com.mcwcapsule.VJVM.classloader.JClassLoader;
import com.mcwcapsule.VJVM.vm.VJVM;

import lombok.val;

public class NonArrayClass extends JClass {
    public NonArrayClass(DataInput dataInput, JClassLoader initLoader) {
        super(dataInput, initLoader);
    }

    public int createInstance() {
        assert getInitState() == InitState.INITIALIZED;
        val heap = VJVM.getHeap();
        int addr = heap.allocate(instanceSize);
        // set class index
        heap.getSlots().setInt(addr - 1, methodAreaIndex);
        return addr;
    }
}
