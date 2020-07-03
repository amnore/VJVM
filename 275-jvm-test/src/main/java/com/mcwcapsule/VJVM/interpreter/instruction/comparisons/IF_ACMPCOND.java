package com.mcwcapsule.VJVM.interpreter.instruction.comparisons;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.runtime.JThread;
import com.mcwcapsule.VJVM.utils.BiIntPredicate;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class IF_ACMPCOND extends Instruction {
    private final BiIntPredicate pred;

    @Override
    public void fetchAndRun(JThread thread) {
        val stack = thread.getCurrentFrame().getOpStack();
        val right = stack.popInt();
        val left = stack.popInt();
        val pc = thread.getPC();
        val offset = pc.getShort();
        if (pred.test(left, right))
            pc.move(offset - 3);
    }

}
