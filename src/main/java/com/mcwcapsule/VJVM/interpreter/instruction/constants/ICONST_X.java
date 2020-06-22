package com.mcwcapsule.VJVM.interpreter.instruction.constants;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ICONST_X extends Instruction {
    private final int value;

    @Override
    public void fetchAndRun(JThread thread) {
        thread.getCurrentFrame().getOpStack().pushFloat(value);
    }

}
