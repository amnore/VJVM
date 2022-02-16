package vjvm.interpreter.instruction.constants;

import lombok.RequiredArgsConstructor;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

@RequiredArgsConstructor
public class FCONST_X extends Instruction {
	private final float value;

	@Override
	public void fetchAndRun(JThread thread) {
		thread.top().stack().pushFloat(value);
	}

}
