package vjvm.interpreter.instruction.constants;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import lombok.val;

public class ACONST_NULL extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        var opStack = thread.currentFrame().opStack();
        opStack.pushAddress(0);
    }

}
