package vjvm.interpreter.instruction.stack;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

public class POP2 extends Instruction {

	@Override
	public void fetchAndRun(JThread thread) {
		thread.top().stack().popLong();
	}

}
