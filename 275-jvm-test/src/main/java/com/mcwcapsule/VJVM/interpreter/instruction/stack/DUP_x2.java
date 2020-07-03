package com.mcwcapsule.VJVM.interpreter.instruction.stack;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;
import lombok.val;

public class DUP_x2 extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        val stack = thread.getCurrentFrame().getOpStack();
        val value = stack.popInt();
        val value2 = stack.popLong();
        stack.pushInt(value);
        stack.pushLong(value2);
        stack.pushInt(value);
    }

}
