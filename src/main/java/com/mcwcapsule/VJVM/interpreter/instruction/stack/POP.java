package com.mcwcapsule.VJVM.interpreter.instruction.stack;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;

public class POP extends Instruction {
    @Override
    public void fetchAndRun(JThread thread) {
        thread.getCurrentFrame().getOpStack().popInt();
    }

}
