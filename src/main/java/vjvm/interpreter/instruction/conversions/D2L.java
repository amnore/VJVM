package vjvm.interpreter.instruction.conversions;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

public class D2L extends Instruction {

  @Override
  public void fetchAndRun(JThread thread) {
    var stack = thread.top().stack();
    stack.pushLong((long) stack.popDouble());
  }

}
