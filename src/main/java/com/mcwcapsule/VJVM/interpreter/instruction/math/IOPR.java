package com.mcwcapsule.VJVM.interpreter.instruction.math;

import java.util.function.IntBinaryOperator;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class IOPR extends Instruction {
    private final IntBinaryOperator opr;

    @Override
    public void fetchAndRun(JThread thread) {
        // TODO: fetch and run
    }

}
