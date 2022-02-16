package vjvm.interpreter.instruction.stack;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

public class DUP_x2 extends Instruction {

	@Override
	public void fetchAndRun(JThread thread) {
		var stack = thread.top().stack();
		var value = stack.popInt();
		var value2 = stack.popLong();
		stack.pushInt(value);
		stack.pushLong(value2);
		stack.pushInt(value);
	}

}
