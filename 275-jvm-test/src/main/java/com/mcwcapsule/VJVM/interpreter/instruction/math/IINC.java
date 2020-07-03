package com.mcwcapsule.VJVM.interpreter.instruction.math;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;
import lombok.val;

public class IINC extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        val slots = thread.getCurrentFrame().getLocalVars();
        val index = thread.getPC().getUnsignedByte();
        slots.setInt(index, slots.getInt(index) + thread.getPC().getByte());
    }

}
