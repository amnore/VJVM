package vjvm.interpreter.instruction.control;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import lombok.val;

public class RETURN2S extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        var ret = thread.currentFrame().opStack().popLong();
        thread.popFrame();
        thread.currentFrame().opStack().pushLong(ret);
    }

}
