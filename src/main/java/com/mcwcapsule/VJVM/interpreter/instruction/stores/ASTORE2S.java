package com.mcwcapsule.VJVM.interpreter.instruction.stores;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;
import com.mcwcapsule.VJVM.vm.VJVM;
import com.mcwcapsule.VJVM.runtime.ArrayClass;

import lombok.val;

public class ASTORE2S extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        val stack = thread.getCurrentFrame().getOpStack();
        val value = stack.popInt();
        val index = stack.popInt();
        val ref = stack.popAddress();
        val heap = VJVM.getHeap();
        val slots = heap.getSlots();
        val jClass = (ArrayClass) heap.getJClass(slots.getInt(index - 1));
        // the address of the element is ref + sizeof(ArrayClass) + index * 2
        slots.setLong(ref + jClass.getInstanceSize() + index * 2, value);
    }

}
