package vjvm.interpreter.instruction.references;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.constant.ClassRef;

public class INSTANCEOF extends Instruction {

	@Override
	public void fetchAndRun(JThread thread) {
		var frame = thread.top();
		var stack = frame.stack();
		var classRef = (ClassRef) frame.link().constant(thread.pc().ushort());
		var obj = stack.popAddress();
		if (obj == 0) {
			stack.pushInt(0);
			return;
		}

		var jClass = classRef.value();
		var objClass = thread.context().heap().get(obj).type();
		stack.pushInt(objClass.castableTo(jClass) ? 1 : 0);
	}

}
