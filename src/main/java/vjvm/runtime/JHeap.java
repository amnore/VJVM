package vjvm.runtime;

import vjvm.vm.VMContext;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;

public class JHeap {
    private static final int extraSize = 2;
    private final ArrayList<JClass> methodArea;
    @Getter
    private final Slots slots;
    private int current = 0;
    private final HashMap<StringWrapper, Integer> internPool;
    private final VMContext context;

    public JHeap(int heapSize, VMContext ctx) {
        methodArea = new ArrayList<>();
        slots = new Slots(heapSize);
        internPool = new HashMap<>();
        context = ctx;
    }

    public int addJClass(JClass jClass) {
        methodArea.add(jClass);
        return methodArea.size() - 1;
    }

    public JClass jClass(int index) {
        try {
            return methodArea.get(index);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int internString(int str) {
        var wrapper = new StringWrapper(str, context);
        var addr = internPool.get(wrapper);
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
        slots.int_(current, size);
        current += extraSize + size;
        return ret;
    }

}

class StringWrapper {
    int arrOffset = -1;
    int lengthOffset = -1;
    Slots slots;

    int array;
    int hash;

    public StringWrapper(int str, VMContext ctx) {
        if (arrOffset == -1)
            arrOffset = ctx.bootstrapLoader().definedClass("java/lang/String")
                .findField("value", "[B").offset();
        if (lengthOffset == -1)
            lengthOffset = ctx.bootstrapLoader().definedClass("[B").instanceSize() - 1;
        if (slots == null)
            slots = ctx.heap().slots();
        array = slots.addressAt(str + arrOffset);
        hash = calcHash();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StringWrapper that = (StringWrapper) o;
        if (hash != that.hash)
            return false;
        int len = slots.int_(array + lengthOffset);
        if (len != slots.int_(that.array + lengthOffset))
            return false;
        for (int i = lengthOffset + 1; i <= lengthOffset + len; ++i)
            if (slots.int_(i + array) != slots.int_(i + that.array))
                return false;
        return true;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    private int calcHash() {
        int len = slots.int_(array + lengthOffset);
        int hash = 0;
        for (int i = array + lengthOffset + 1; i <= array + lengthOffset + len; ++i)
            hash = hash * 31 + slots.int_(i);
        return hash;
    }
}
