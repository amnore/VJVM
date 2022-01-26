package vjvm.interpreter.instruction.math;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

public class INEG extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        var stack = thread.top().stack();
        stack.pushInt(-stack.popInt());
    }

}
