package com.mcwcapsule.VJVM.interpreter.instruction.comparisons;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;
import com.mcwcapsule.VJVM.utils.BiIntPredicate;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IF_ACMPCOND extends Instruction {
    private final BiIntPredicate pred;

    @Override
    public void fetchAndRun(JThread thread) {
        // TODO: fetch and run
    }

}
