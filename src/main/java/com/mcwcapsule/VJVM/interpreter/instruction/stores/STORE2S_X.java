package com.mcwcapsule.VJVM.interpreter.instruction.stores;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class STORE2S_X extends Instruction {
    private final int index;

    @Override
    public void fetchAndRun(JThread thread) {
        // TODO: fetch and run
    }

}
