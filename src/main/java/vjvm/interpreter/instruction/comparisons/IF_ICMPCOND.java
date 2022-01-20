package vjvm.interpreter.instruction.comparisons;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.utils.BiIntPredicate;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class IF_ICMPCOND extends Instruction {
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
