package com.mcwcapsule.VJVM.interpreter.instruction.stack;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class POPX extends Instruction {
    private final int size;

    @Override
    public void fetchAndRun(JThread thread) {
        // TODO: fetch and run
    }

}
