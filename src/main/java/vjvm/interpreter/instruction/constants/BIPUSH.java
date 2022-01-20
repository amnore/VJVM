package vjvm.interpreter.instruction.constants;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

public class BIPUSH extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        var opStack = thread.currentFrame().opStack();
        var value = thread.pc().byte_();
        opStack.pushInt(value);
    }

}
