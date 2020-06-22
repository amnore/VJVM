package com.mcwcapsule.VJVM.interpreter.instruction.loads;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class LOAD1S_X extends Instruction {
    private final int index;

    @Override
    public void fetchAndRun(JThread thread) {
        val frame = thread.getCurrentFrame();
        frame.getOpStack().pushInt(frame.getLocalVars().getInt(index));
    }

}
