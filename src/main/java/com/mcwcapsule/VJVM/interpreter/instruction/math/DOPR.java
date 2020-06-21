package com.mcwcapsule.VJVM.interpreter.instruction.math;

import java.util.function.DoubleBinaryOperator;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DOPR extends Instruction {
    private final DoubleBinaryOperator opr;

    @Override
    public void fetchAndRun(JThread thread) {
        // TODO: fetch and run
    }

}
