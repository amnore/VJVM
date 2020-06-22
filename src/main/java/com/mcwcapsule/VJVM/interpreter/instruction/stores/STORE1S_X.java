package com.mcwcapsule.VJVM.interpreter.instruction.stores;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;

import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class STORE1S_X extends Instruction {
    private final int index;

    @Override
    public void fetchAndRun(JThread thread) {
        val frame = thread.getCurrentFrame();
        frame.getLocalVars().setInt(index, frame.getOpStack().popInt());
    }

}
