package vjvm.interpreter.instruction.comparisons;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

public class FCMPL extends Instruction {

	@Override
	public void fetchAndRun(JThread thread) {
		var stack = thread.top().stack();
		var right = stack.popFloat();
		var left = stack.popFloat();
		stack.pushInt(Float.compare(left, right));
	}

}
