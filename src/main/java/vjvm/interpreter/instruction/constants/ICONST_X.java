package vjvm.interpreter.instruction.constants;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ICONST_X extends Instruction {
    private final int value;

    @Override
    public void fetchAndRun(JThread thread) {
        thread.getCurrentFrame().getOpStack().pushInt(value);
    }

}
