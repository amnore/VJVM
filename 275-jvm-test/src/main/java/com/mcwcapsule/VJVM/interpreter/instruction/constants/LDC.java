package com.mcwcapsule.VJVM.interpreter.instruction.constants;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;
import com.mcwcapsule.VJVM.runtime.classdata.constant.ValueConstant;
import lombok.val;

public class LDC extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        val frame = thread.getCurrentFrame();
        val stack = frame.getOpStack();
        val index = thread.getPC().getUnsignedByte();
        val value = ((ValueConstant) frame.getDynLink().getConstant(index)).getValue();

        // only int, String and float are supported
        if (value instanceof Integer)
            stack.pushInt((Integer) value);
        else if (value instanceof Float)
            stack.pushFloat((Float) value);
    }

}
