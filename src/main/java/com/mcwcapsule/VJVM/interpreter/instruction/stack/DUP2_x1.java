package com.mcwcapsule.VJVM.interpreter.instruction.stack;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;
import lombok.val;

public class DUP2_x1 extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        val stack = thread.getCurrentFrame().getOpStack();
        val value = stack.popLong();
        val value2 = stack.popInt();
        stack.pushLong(value);
        stack.pushInt(value2);
        stack.pushLong(value);
    }

}
