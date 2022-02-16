package vjvm.interpreter.instruction.references;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

public class ATHROW extends Instruction {
	@Override
	public void fetchAndRun(JThread thread) {
		var ctx = thread.context();
		var addr = thread.top().stack().popAddress();

		assert addr != 0;
		thread.exception(ctx.heap().get(addr));
	}
}
