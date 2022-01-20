package vjvm.interpreter.instruction.math;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.utils.FloatBinaryOperator;
import lombok.AllArgsConstructor;
import lombok.val;

@AllArgsConstructor
public class FOPR extends Instruction {
    private final FloatBinaryOperator opr;

    @Override
    public void fetchAndRun(JThread thread) {
        val stack = thread.getCurrentFrame().getOpStack();
        val right = stack.popFloat();
        val left = stack.popFloat();
        stack.pushFloat(opr.applyAsFloat(left, right));
    }

}
