package com.mcwcapsule.VJVM.interpreter.instruction.constants;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;

import lombok.val;

public class BIPUSH extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        val opStack = thread.getCurrentFrame().getOpStack();
        val value = thread.getPC().getByte();
        opStack.pushInt(value);
    }

}
