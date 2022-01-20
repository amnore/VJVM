package vjvm.interpreter.instruction.conversions;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import lombok.val;

public class I2D extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        var stack = thread.currentFrame().opStack();
        stack.pushDouble(stack.popInt());
    }

}
