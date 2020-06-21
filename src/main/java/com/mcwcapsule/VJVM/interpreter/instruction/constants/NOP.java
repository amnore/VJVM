package com.mcwcapsule.VJVM.interpreter.instruction.constants;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;

import lombok.val;

public class NOP extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        // do nothing
    }

}
