package com.mcwcapsule.VJVM.interpreter.instruction.references;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;
import com.mcwcapsule.VJVM.runtime.classdata.constant.ClassRef;
import com.mcwcapsule.VJVM.vm.VJVM;
import lombok.val;

public class CHECKCAST extends Instruction {
    @Override
    public void fetchAndRun(JThread thread) {
        val frame = thread.getCurrentFrame();
        val stack = frame.getOpStack();
        val classRef = (ClassRef) frame.getDynLink().getConstant(thread.getPC().getUnsignedShort());
        val obj = stack.popAddress();
        if (obj == 0) {
            stack.pushAddress(obj);
            return;
        }
        try {
            classRef.resolve(frame.getJClass());
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }
        val jClass = classRef.getJClass();
        val objClass = VJVM.getHeap().getJClass(VJVM.getHeap().getSlots().getInt(obj - 1));
        System.err.println(jClass.getThisClass().getName());
        System.err.println(objClass.getThisClass().getName());
        if (!objClass.canCastTo(jClass))
            throw new ClassCastException();
        stack.pushAddress(obj);
    }
}
