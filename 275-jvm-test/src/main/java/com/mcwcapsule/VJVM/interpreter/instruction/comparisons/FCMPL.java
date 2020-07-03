package com.mcwcapsule.VJVM.interpreter.instruction.comparisons;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;
import lombok.val;

public class FCMPL extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        val stack = thread.getCurrentFrame().getOpStack();
        val right = stack.popFloat();
        val left = stack.popFloat();
        stack.pushInt(Float.compare(left, right));
    }

}
