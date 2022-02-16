package vjvm.interpreter.instruction.conversions;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

public class L2D extends Instruction {

	@Override
	public void fetchAndRun(JThread thread) {
		var stack = thread.top().stack();
		stack.pushDouble((double) stack.popLong());
	}

}
