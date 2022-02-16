package vjvm.interpreter.instruction.control;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

public class RETURN extends Instruction {

	@Override
	public void fetchAndRun(JThread thread) {
		thread.pop();
	}

}
