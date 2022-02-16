package vjvm.interpreter.instruction.control;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

import static vjvm.classfiledefs.Descriptors.*;

public class RETURN1S extends Instruction {

	@Override
	public void fetchAndRun(JThread thread) {
		var ret = thread.top().stack().popInt();
		var returnType = thread.top().method().descriptor().charAt(0);
		switch (returnType) {
			case DESC_boolean -> ret &= 1;
			case DESC_byte -> ret = (byte) ret;
			case DESC_char -> ret = (char) ret;
			case DESC_short -> ret = (short) ret;
		}
		thread.pop();
		thread.top().stack().pushInt(ret);
	}

}
