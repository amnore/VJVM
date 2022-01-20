package vjvm.interpreter.instruction.stores;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class STORE2S_X extends Instruction {
    private final int index;

    @Override
    public void fetchAndRun(JThread thread) {
        var frame = thread.currentFrame();
        frame.localVars().long_(index, frame.opStack().popLong());
    }

}
