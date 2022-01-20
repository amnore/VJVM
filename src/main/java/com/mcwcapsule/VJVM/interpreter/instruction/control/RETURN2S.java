package com.mcwcapsule.VJVM.interpreter.instruction.control;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;
import lombok.val;

public class RETURN2S extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        val ret = thread.getCurrentFrame().getOpStack().popLong();
        thread.popFrame();
        thread.getCurrentFrame().getOpStack().pushLong(ret);
    }

}
