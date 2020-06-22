package com.mcwcapsule.VJVM.interpreter.instruction.math;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;
import com.mcwcapsule.VJVM.utils.FloatBinaryOperator;
import lombok.AllArgsConstructor;
import lombok.val;

@AllArgsConstructor
public class FOPR extends Instruction {
    private final FloatBinaryOperator opr;

    @Override
    public void fetchAndRun(JThread thread) {
        val stack = thread.getCurrentFrame().getOpStack();
        val right = stack.popFloat();
        val left = stack.popFloat();
        stack.pushDouble(opr.applyAsFloat(left, right));
    }

}
