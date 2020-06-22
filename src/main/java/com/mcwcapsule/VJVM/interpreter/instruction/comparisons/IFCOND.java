package com.mcwcapsule.VJVM.interpreter.instruction.comparisons;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;
import lombok.RequiredArgsConstructor;

import java.util.function.IntPredicate;

@RequiredArgsConstructor
public class IFCOND extends Instruction {
    private final IntPredicate pred;

    @Override
    public void fetchAndRun(JThread thread) {
        // TODO: fetch and run
    }

}
