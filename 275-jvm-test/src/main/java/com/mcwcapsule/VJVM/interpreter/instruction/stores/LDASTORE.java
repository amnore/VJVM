package com.mcwcapsule.VJVM.interpreter.instruction.stores;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.ArrayClass;
import com.mcwcapsule.VJVM.runtime.JThread;
import com.mcwcapsule.VJVM.vm.VJVM;
import lombok.val;

public class LDASTORE extends Instruction {
    @Override
    public void fetchAndRun(JThread thread) {
        val stack = thread.getCurrentFrame().getOpStack();
        val value = stack.popLong();
        val index = stack.popInt();
        val obj = stack.popAddress();
        val heap = VJVM.getHeap();
        val slots = heap.getSlots();
        val jClass = (ArrayClass) heap.getJClass(slots.getInt(obj - 1));
        // the address of the element is obj + sizeof(ArrayClass) + index * 2
        slots.setLong(obj + jClass.getInstanceSize() + index * 2, value);
    }
}
