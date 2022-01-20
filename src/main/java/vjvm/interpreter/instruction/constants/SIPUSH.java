package vjvm.interpreter.instruction.constants;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

public class SIPUSH extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        var opStack = thread.currentFrame().opStack();
        var value = thread.pc().short_();
        opStack.pushInt(value);
    }

}
