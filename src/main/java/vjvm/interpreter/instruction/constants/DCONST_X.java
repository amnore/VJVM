package vjvm.interpreter.instruction.constants;

import lombok.RequiredArgsConstructor;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

@RequiredArgsConstructor
public class DCONST_X extends Instruction {
	private final double value;

	@Override
	public void fetchAndRun(JThread thread) {
		thread.top().stack().pushDouble(value);
	}

}
