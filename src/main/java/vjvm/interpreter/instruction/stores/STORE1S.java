package vjvm.interpreter.instruction.stores;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

public class STORE1S extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        var frame = thread.top();
        frame.vars().int_(thread.pc().ubyte(), frame.stack().popInt());
    }

}
