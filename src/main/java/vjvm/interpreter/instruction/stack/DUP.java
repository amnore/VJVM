package vjvm.interpreter.instruction.stack;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

public class DUP extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        var stack = thread.top().stack();
        var value = stack.popInt();
        stack.pushInt(value);
        stack.pushInt(value);
    }

}
