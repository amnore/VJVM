package vjvm.interpreter.instruction.constants;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import lombok.val;

public class BIPUSH extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        val opStack = thread.getCurrentFrame().getOpStack();
        val value = thread.getPC().getByte();
        opStack.pushInt(value);
    }

}
