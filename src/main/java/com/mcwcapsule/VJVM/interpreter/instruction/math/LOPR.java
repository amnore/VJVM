package com.mcwcapsule.VJVM.interpreter.instruction.math;

import java.util.function.LongBinaryOperator;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LOPR extends Instruction {
    private final LongBinaryOperator opr;

    @Override
    public void fetchAndRun(JThread thread) {
        // TODO: fetch and run
    }

}