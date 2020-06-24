package com.mcwcapsule.VJVM.interpreter.instruction.references;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;
import com.mcwcapsule.VJVM.runtime.classdata.MethodInfo;
import com.mcwcapsule.VJVM.runtime.classdata.constant.MethodRef;
import com.mcwcapsule.VJVM.utils.CallUtil;
import com.mcwcapsule.VJVM.vm.VJVM;
import lombok.val;

public class INVOKEINTERFACE extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        val frame = thread.getCurrentFrame();
        val pc = thread.getPC();
        val methodRef = (MethodRef) frame.getDynLink().getConstant(pc.getUnsignedShort());
        val argc = methodRef.getArgc();
        pc.getUnsignedShort();
        assert pc.getByte() == 0;
        try {
            methodRef.resolve(frame.getJClass());
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }

        // select the method to call, see spec. 5.4.6
        MethodInfo method;
        if (methodRef.getInfo().isPrivate())
            method = methodRef.getInfo();
        else {
            val heap = VJVM.getHeap();
            val stack = frame.getOpStack();
            val obj = stack.getSlots().getAddress(stack.getTop() - methodRef.getArgc() - 1);
            val objClass = heap.getJClass(heap.getSlots().getInt(obj - 1));
            method = objClass.findMethod(methodRef.getName(), methodRef.getDescriptor());
        }
        CallUtil.callMethod(method, thread);
    }

}
