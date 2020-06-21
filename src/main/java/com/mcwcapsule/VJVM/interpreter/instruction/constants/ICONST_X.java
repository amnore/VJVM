package com.mcwcapsule.VJVM.interpreter.instruction.constants;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;

import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class ICONST_X extends Instruction {
    private final int value;

    @Override
    public void fetchAndRun(JThread thread) {
        // TODO: fetch and run
    }

}
