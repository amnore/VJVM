package vjvm.interpreter.instruction.loads;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

public class LOAD2S extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        var frame = thread.currentFrame();
        frame.opStack().pushLong(frame.localVars().long_(thread.pc().ubyte()));
    }

}
