package vjvm.interpreter.instruction.stack;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import lombok.val;

public class DUP2 extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        var stack = thread.currentFrame().opStack();
        var value = stack.popLong();
        stack.pushLong(value);
        stack.pushLong(value);
    }

}
