package vjvm.interpreter.instruction.math;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

public class IINC extends Instruction {

    @Override
    public void fetchAndRun(JThread thread) {
        var slots = thread.top().vars();
        var index = thread.pc().ubyte();
        slots.int_(index, slots.int_(index) + thread.pc().byte_());
    }

}
