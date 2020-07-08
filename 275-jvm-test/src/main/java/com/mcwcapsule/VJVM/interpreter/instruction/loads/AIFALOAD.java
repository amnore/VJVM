package com.mcwcapsule.VJVM.interpreter.instruction.loads;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;
import com.mcwcapsule.VJVM.vm.VJVM;
import lombok.val;

public class AIFALOAD extends Instruction {
    @Override
    public void fetchAndRun(JThread thread) {
        val stack = thread.getCurrentFrame().getOpStack();
        val index = stack.popInt();
        val obj = stack.popAddress();
        val heap = VJVM.getHeap();
        val slots = heap.getSlots();
        val jClass = heap.getJClass(slots.getInt(obj - 1));
        assert jClass.isArray();
        // the address of the element is obj + sizeof(ArrayClass) + index
        val value = slots.getInt(obj + jClass.getInstanceSize() + index);
        stack.pushInt(value);
    }
}
