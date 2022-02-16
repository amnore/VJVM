package vjvm.interpreter.instruction.control;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

public class GOTO extends Instruction {

	@Override
	public void fetchAndRun(JThread thread) {
		var pc = thread.pc();
		pc.move(pc.short_() - INSTR_LENGTH);
	}

}
