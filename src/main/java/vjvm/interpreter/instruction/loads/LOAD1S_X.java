package vjvm.interpreter.instruction.loads;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LOAD1S_X extends Instruction {
    private final int index;

    @Override
    public void fetchAndRun(JThread thread) {
        var frame = thread.currentFrame();
        frame.opStack().pushInt(frame.localVars().int_(index));
    }

}
