package vjvm.interpreter.instruction.constants;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DCONST_X extends Instruction {
    private final double value;

    @Override
    public void fetchAndRun(JThread thread) {
        thread.getCurrentFrame().getOpStack().pushDouble(value);
    }

}
