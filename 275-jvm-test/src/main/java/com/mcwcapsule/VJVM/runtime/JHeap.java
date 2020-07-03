package com.mcwcapsule.VJVM.runtime;

import com.mcwcapsule.VJVM.vm.VJVM;
import lombok.Getter;
import lombok.val;

import java.util.ArrayList;
import java.util.HashMap;

public class JHeap {
    private static final int extraSize = 2;
    private final ArrayList<JClass> methodArea;
    @Getter
    private final Slots slots;
    private int current = 0;
    private final HashMap<StringWrapper, Integer> internPool;

    public JHeap(int heapSize) {
        methodArea = new ArrayList<>();
        slots = new Slots(heapSize);
        internPool = new HashMap<>();
    }

    public int addJClass(JClass jClass) {
        methodArea.add(jClass);
        return methodArea.size() - 1;
    }

    public JClass getJClass(int index) {
        return methodArea.get(index);
    }

    public int getInternString(int str) {
        val wrapper = new StringWrapper(str);
        val addr = internPool.get(wrapper);
        if (addr == null) {
            internPool.put(wrapper, str);
            return str;
        } else return addr;
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

class StringWrapper {
    static int arrOffset = -1;
    static int lengthOffset = -1;
    static Slots slots;

    int array;
    int hash;

    public StringWrapper(int str) {
        if (arrOffset == -1)
            arrOffset = VJVM.getBootstrapLoader().getDefinedClass("java/lang/String")
                .findField("value", "[C").getOffset();
        if (lengthOffset == -1)
            lengthOffset = VJVM.getBootstrapLoader().getDefinedClass("[C").getInstanceSize() - 1;
        if (slots == null)
            slots = VJVM.getHeap().getSlots();
        array = slots.getAddress(str + arrOffset);
        hash = calcHash();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringWrapper that = (StringWrapper) o;
        if (hash != that.hash)
            return false;
        int len = slots.getInt(array + lengthOffset);
        if (len != slots.getInt(that.array + lengthOffset))
            return false;
        for (int i = lengthOffset + 1; i <= lengthOffset + len; ++i)
            if (slots.getInt(i + array) != slots.getInt(i + that.array))
                return false;
        return true;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    private int calcHash() {
        int len = slots.getInt(array + lengthOffset);
        int hash = 0;
        for (int i = array + lengthOffset + 1; i <= array + lengthOffset + len; ++i)
            hash = hash * 31 + slots.getInt(i);
        return hash;
    }
}
