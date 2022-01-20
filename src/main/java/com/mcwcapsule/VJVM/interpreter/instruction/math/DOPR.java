package com.mcwcapsule.VJVM.interpreter.instruction.math;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;
import lombok.AllArgsConstructor;
import lombok.val;

import java.util.function.DoubleBinaryOperator;

@AllArgsConstructor
public class DOPR extends Instruction {
    private final DoubleBinaryOperator opr;

    @Override
    public void fetchAndRun(JThread thread) {
        val stack = thread.getCurrentFrame().getOpStack();
        val right = stack.popDouble();
        val left = stack.popDouble();
        stack.pushDouble(opr.applyAsDouble(left, right));
    }

}
