package vjvm.interpreter.instruction.loads;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

public class LOAD1S extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        var frame = thread.currentFrame();
        frame.opStack().pushInt(frame.localVars().int_(thread.pc().ubyte()));
    }

}
