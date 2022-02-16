package vjvm.interpreter.instruction.math;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

public class LSHR extends Instruction {

	@Override
	public void fetchAndRun(JThread thread) {
		var stack = thread.top().stack();
		var v2 = stack.popInt();
		var v1 = stack.popLong();
		stack.pushLong(v1 >> v2);
	}

}
