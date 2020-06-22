package com.mcwcapsule.VJVM.interpreter.instruction.stack;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;
import lombok.val;

public class DUP extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        val stack = thread.getCurrentFrame().getOpStack();
        val value = stack.popInt();
        stack.pushInt(value);
        stack.pushInt(value);
    }

}
