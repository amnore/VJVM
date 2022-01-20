package com.mcwcapsule.VJVM.interpreter.instruction.comparisons;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;
import lombok.val;

public class DCMPL extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        val stack = thread.getCurrentFrame().getOpStack();
        val right = stack.popDouble();
        val left = stack.popDouble();
        stack.pushInt(Double.compare(left, right));
    }

}
