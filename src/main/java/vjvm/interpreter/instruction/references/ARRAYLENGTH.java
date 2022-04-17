package vjvm.interpreter.instruction.references;

import lombok.var;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.ProgramCounter;
import vjvm.runtime.classdata.MethodInfo;
import vjvm.runtime.object.ArrayObject;

public class ARRAYLENGTH extends Instruction {
  public ARRAYLENGTH(ProgramCounter pc, MethodInfo method) {
  }

  @Override
  public void run(JThread thread) {
    var stack = thread.top().stack();
    var obj = thread.context().heap().get(stack.popAddress());

    assert obj.type().array();
    stack.pushInt(((ArrayObject) obj).length());
  }

  @Override
  public String toString() {
    return "arraylength";
  }
}
