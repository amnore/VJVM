package vjvm.interpreter.instruction.control;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import lombok.val;

public class GOTO extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        val pc = thread.getPC();
        pc.move(pc.getShort() - 3);
    }

}
