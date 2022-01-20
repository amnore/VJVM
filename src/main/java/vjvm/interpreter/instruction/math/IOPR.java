package vjvm.interpreter.instruction.math;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import lombok.AllArgsConstructor;
import lombok.val;

import java.util.function.IntBinaryOperator;

@AllArgsConstructor
public class IOPR extends Instruction {
    private final IntBinaryOperator opr;

    @Override
    public void fetchAndRun(JThread thread) {
        var stack = thread.currentFrame().opStack();
        var right = stack.popInt();
        var left = stack.popInt();
        stack.pushInt(opr.applyAsInt(left, right));
    }

}
