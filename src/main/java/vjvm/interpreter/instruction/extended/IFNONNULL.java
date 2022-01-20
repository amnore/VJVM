package vjvm.interpreter.instruction.extended;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

public class IFNONNULL extends Instruction {
    @Override
    public void fetchAndRun(JThread thread) {
        var value = thread.currentFrame().opStack().popAddress();
        var offset = thread.pc().short_();
        if (value != 0)
            thread.pc().move(offset - 3);
    }
}
