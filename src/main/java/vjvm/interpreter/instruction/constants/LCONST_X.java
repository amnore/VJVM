package vjvm.interpreter.instruction.constants;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LCONST_X extends Instruction {
    private final long value;

    @Override
    public void fetchAndRun(JThread thread) {
        thread.currentFrame().opStack().pushLong(value);
    }

}
