package com.mcwcapsule.VJVM.interpreter.instruction.math;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;
import lombok.AllArgsConstructor;

import java.util.function.IntBinaryOperator;

@AllArgsConstructor
public class IOPR extends Instruction {
    private final IntBinaryOperator opr;

    @Override
    public void fetchAndRun(JThread thread) {
        // TODO: fetch and run
    }

}
