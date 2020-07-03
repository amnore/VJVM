package com.mcwcapsule.VJVM.interpreter.instruction.conversions;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;
import lombok.val;

public class D2I extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        val stack = thread.getCurrentFrame().getOpStack();
        stack.pushInt((int) stack.popDouble());
    }

}
