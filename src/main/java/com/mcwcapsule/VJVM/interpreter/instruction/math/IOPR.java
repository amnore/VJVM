package com.mcwcapsule.VJVM.interpreter.instruction.math;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;
import lombok.AllArgsConstructor;
import lombok.val;

import java.util.function.IntBinaryOperator;

@AllArgsConstructor
public class IOPR extends Instruction {
    private final IntBinaryOperator opr;

    @Override
    public void fetchAndRun(JThread thread) {
        val stack = thread.getCurrentFrame().getOpStack();
        val right = stack.popInt();
        val left = stack.popInt();
        stack.pushDouble(opr.applyAsInt(left, right));
    }

}
