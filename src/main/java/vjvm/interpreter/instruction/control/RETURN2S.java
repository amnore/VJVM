package vjvm.interpreter.instruction.control;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import lombok.val;

public class RETURN2S extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        val ret = thread.getCurrentFrame().getOpStack().popLong();
        thread.popFrame();
        thread.getCurrentFrame().getOpStack().pushLong(ret);
    }

}
