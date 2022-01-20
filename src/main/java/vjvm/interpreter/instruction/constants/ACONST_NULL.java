package vjvm.interpreter.instruction.constants;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import lombok.val;

public class ACONST_NULL extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        val opStack = thread.getCurrentFrame().getOpStack();
        opStack.pushAddress(0);
    }

}
