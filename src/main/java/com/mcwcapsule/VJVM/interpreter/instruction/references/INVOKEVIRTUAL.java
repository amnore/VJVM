package com.mcwcapsule.VJVM.interpreter.instruction.references;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;
import com.mcwcapsule.VJVM.runtime.classdata.constant.MethodRef;
import com.mcwcapsule.VJVM.utils.CallUtil;
import com.mcwcapsule.VJVM.vm.VJVM;
import lombok.val;

public class INVOKEVIRTUAL extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        val frame = thread.getCurrentFrame();
        val methodRef = (MethodRef) frame.getDynLink().getConstant(thread.getPC().getUnsignedShort());
        try {
            methodRef.resolve(frame.getJClass());
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }
        val heap = VJVM.getHeap();
        val stack = frame.getOpStack();
        val objClass = heap.getJClass(heap.getSlots().getInt(stack.getSlots().getInt(stack.getTop() - methodRef.getArgc() - 1)));
        val method = objClass.findMethod(methodRef.getName(), methodRef.getDescriptor());
        CallUtil.callMethod(objClass, method, thread);
    }

}
