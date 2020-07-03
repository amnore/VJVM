package com.mcwcapsule.VJVM.interpreter.instruction.comparisons;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.function.IntPredicate;

@RequiredArgsConstructor
public class IFCOND extends Instruction {
    private final IntPredicate pred;

    @Override
    public void fetchAndRun(JThread thread) {
        val stack = thread.getCurrentFrame().getOpStack();
        val value = stack.popInt();
        val pc = thread.getPC();
        val offset = pc.getShort();
        if (pred.test(value))
            pc.move(offset - 3);
    }

}
