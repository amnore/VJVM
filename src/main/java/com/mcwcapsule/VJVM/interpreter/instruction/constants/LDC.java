package com.mcwcapsule.VJVM.interpreter.instruction.constants;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;
import com.mcwcapsule.VJVM.runtime.classdata.constant.ClassRef;
import com.mcwcapsule.VJVM.runtime.classdata.constant.ValueConstant;
import lombok.val;

public class LDC extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        val frame = thread.getCurrentFrame();
        val stack = frame.getOpStack();
        val index = thread.getPC().getUnsignedByte();
        val constant = frame.getDynLink().getConstant(index);
        if (constant instanceof ClassRef) {
            try {
                ((ClassRef) constant).resolve(frame.getJClass());
            } catch (ClassNotFoundException e) {
                throw new Error(e);
            }
            stack.pushAddress(((ClassRef) constant).getJClass().getClassObject());
        } else {
            val value = ((ValueConstant) frame.getDynLink().getConstant(index)).getValue();
            if (value instanceof Integer)
                stack.pushInt((Integer) value);
            else if (value instanceof Float)
                stack.pushFloat((Float) value);
        }
    }

}
