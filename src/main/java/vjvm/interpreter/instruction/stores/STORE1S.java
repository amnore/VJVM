package vjvm.interpreter.instruction.stores;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

public class STORE1S extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        var frame = thread.currentFrame();
        frame.localVars().int_(thread.pc().ubyte(), frame.opStack().popInt());
    }

}
