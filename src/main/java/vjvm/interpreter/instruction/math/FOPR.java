package vjvm.interpreter.instruction.math;

import lombok.AllArgsConstructor;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.utils.FloatBinaryOperator;

@AllArgsConstructor
public class FOPR extends Instruction {
	private final FloatBinaryOperator opr;

	@Override
	public void fetchAndRun(JThread thread) {
		var stack = thread.top().stack();
		var right = stack.popFloat();
		var left = stack.popFloat();
		stack.pushFloat(opr.applyAsFloat(left, right));
	}

}
