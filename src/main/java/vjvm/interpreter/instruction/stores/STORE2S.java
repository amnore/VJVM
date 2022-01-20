package vjvm.interpreter.instruction.stores;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

public class STORE2S extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        var frame = thread.currentFrame();
        frame.localVars().long_(thread.pc().ubyte(), frame.opStack().popLong());
    }

}
