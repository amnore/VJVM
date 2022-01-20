package vjvm.interpreter.instruction.math;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import lombok.val;

public class DNEG extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        var stack = thread.currentFrame().opStack();
        stack.pushDouble(-stack.popDouble());
    }

}
