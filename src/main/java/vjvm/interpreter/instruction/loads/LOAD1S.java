package vjvm.interpreter.instruction.loads;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

public class LOAD1S extends Instruction {

	@Override
	public void fetchAndRun(JThread thread) {
		var frame = thread.top();
		frame.stack().pushInt(frame.vars().int_(thread.pc().ubyte()));
	}

}
