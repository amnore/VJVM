package vjvm.interpreter.instruction.stack;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

public class SWAP extends Instruction {

	@Override
	public void fetchAndRun(JThread thread) {
		var stack = thread.top().stack();
		var value = stack.popInt();
		var value2 = stack.popInt();
		stack.pushInt(value);
		stack.pushInt(value2);
	}

}
