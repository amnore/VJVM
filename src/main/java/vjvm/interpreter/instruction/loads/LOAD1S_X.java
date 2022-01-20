package vjvm.interpreter.instruction.loads;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class LOAD1S_X extends Instruction {
    private final int index;

    @Override
    public void fetchAndRun(JThread thread) {
        val frame = thread.getCurrentFrame();
        frame.getOpStack().pushInt(frame.getLocalVars().getInt(index));
    }

}
