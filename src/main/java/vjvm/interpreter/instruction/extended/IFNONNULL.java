package vjvm.interpreter.instruction.extended;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import lombok.val;

public class IFNONNULL extends Instruction {
    @Override
    public void fetchAndRun(JThread thread) {
        val value = thread.getCurrentFrame().getOpStack().popAddress();
        val offset = thread.getPC().getShort();
        if (value != 0)
            thread.getPC().move(offset - 3);
    }
}
