package vjvm.interpreter.instruction.loads;

import lombok.var;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.object.ArrayObject;

public class CSALOAD extends Instruction {
  @Override
  public void fetchAndRun(JThread thread) {
    var stack = thread.top().stack();
    var index = stack.popInt();
    var obj = thread.context().heap().get(stack.popAddress());

    assert obj.type().array();
    stack.pushInt(((ArrayObject) obj).short_(index));
  }
}
