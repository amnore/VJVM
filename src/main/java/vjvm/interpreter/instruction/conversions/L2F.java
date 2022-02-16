package vjvm.interpreter.instruction.conversions;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

public class L2F extends Instruction {

	@Override
	public void fetchAndRun(JThread thread) {
		var stack = thread.top().stack();
		stack.pushFloat((float) stack.popLong());
	}

}
