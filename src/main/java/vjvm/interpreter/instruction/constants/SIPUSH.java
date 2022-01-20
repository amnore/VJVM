package vjvm.interpreter.instruction.constants;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import lombok.val;

public class SIPUSH extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        val opStack = thread.getCurrentFrame().getOpStack();
        val value = thread.getPC().getShort();
        opStack.pushInt(value);
    }

}
