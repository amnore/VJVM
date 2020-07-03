package com.mcwcapsule.VJVM.interpreter.instruction.extended;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;
import lombok.val;

public class IFNONNULL extends Instruction {
    @Override
    public void fetchAndRun(JThread thread) {
        val value = thread.getCurrentFrame().getOpStack().popAddress();
        val offset = thread.getPC().getShort();
        if (value != 0)
            thread.getPC().move(offset - 3);
    }
}
