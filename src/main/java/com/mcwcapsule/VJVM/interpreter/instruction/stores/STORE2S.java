package com.mcwcapsule.VJVM.interpreter.instruction.stores;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;

import lombok.val;

public class STORE2S extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        val frame = thread.getCurrentFrame();
        frame.getLocalVars().setLong(thread.getPC().getUnsignedByte(), frame.getOpStack().popLong());
    }

}
