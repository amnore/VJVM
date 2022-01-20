package vjvm.interpreter.instruction.math;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import lombok.AllArgsConstructor;
import lombok.val;

import java.util.function.LongBinaryOperator;

@AllArgsConstructor
public class LOPR extends Instruction {
    private final LongBinaryOperator opr;

    @Override
    public void fetchAndRun(JThread thread) {
        var stack = thread.currentFrame().opStack();
        var right = stack.popLong();
        var left = stack.popLong();
        stack.pushLong(opr.applyAsLong(left, right));
    }

}
