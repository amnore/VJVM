package com.mcwcapsule.VJVM.interpreter.instruction.loads;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LOAD2S_X extends Instruction {
    private final int index;

    @Override
    public void fetchAndRun(JThread thread) {
        // TODO: fetch and run
    }

}
