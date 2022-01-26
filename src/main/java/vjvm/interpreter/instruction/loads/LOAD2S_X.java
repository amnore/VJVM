package vjvm.interpreter.instruction.loads;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LOAD2S_X extends Instruction {
    private final int index;

    @Override
    public void fetchAndRun(JThread thread) {
        var frame = thread.top();
        frame.stack().pushLong(frame.vars().long_(index));
    }

}
