package com.mcwcapsule.VJVM.interpreter.instruction.math;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;
import lombok.AllArgsConstructor;
import lombok.val;

import java.util.function.LongBinaryOperator;

@AllArgsConstructor
public class LOPR extends Instruction {
    private final LongBinaryOperator opr;

    @Override
    public void fetchAndRun(JThread thread) {
        val stack = thread.getCurrentFrame().getOpStack();
        val right = stack.popLong();
        val left = stack.popLong();
        stack.pushLong(opr.applyAsLong(left, right));
    }

}
