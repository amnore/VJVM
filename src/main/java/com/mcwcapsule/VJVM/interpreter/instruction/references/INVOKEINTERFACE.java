package com.mcwcapsule.VJVM.interpreter.instruction.references;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;
import com.mcwcapsule.VJVM.runtime.metadata.constant.MethodRef;
import com.mcwcapsule.VJVM.utils.CallUtil;
import com.mcwcapsule.VJVM.vm.VJVM;
import lombok.val;

public class INVOKEINTERFACE extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        val frame = thread.getCurrentFrame();
        val pc = thread.getPC();
        val stack = frame.getOpStack();
        val heap = VJVM.getHeap();
        val ref = (MethodRef) frame.getDynLink().getConstant(pc.getUnsignedShort());
        val argc = ref.getArgc();
        pc.getUnsignedShort();
        assert pc.getByte() == 0;
        try {
            ref.resolve(frame.getJClass());
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }
        val obj = stack.getSlots().getAddress(stack.getTop() - argc - 1);
        val jClass = heap.getJClass(heap.getSlots().getInt(obj - 1));
        val method = jClass.findMethod(ref.getName(), ref.getDescriptor());
        CallUtil.callMethod(jClass, method, thread);
    }

}
