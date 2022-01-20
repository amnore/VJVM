package vjvm.interpreter.instruction.stack;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import lombok.val;

public class DUP2_x1 extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        var stack = thread.currentFrame().opStack();
        var value = stack.popLong();
        var value2 = stack.popInt();
        stack.pushLong(value);
        stack.pushInt(value2);
        stack.pushLong(value);
    }

}
