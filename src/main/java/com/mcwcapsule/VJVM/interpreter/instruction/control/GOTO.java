package com.mcwcapsule.VJVM.interpreter.instruction.control;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;
import lombok.val;

public class GOTO extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        val pc = thread.getPC();
        pc.move(pc.getUnsignedShort() - 3);
    }

}
