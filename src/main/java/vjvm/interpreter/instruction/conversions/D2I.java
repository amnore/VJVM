package vjvm.interpreter.instruction.conversions;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

public class D2I extends Instruction {

  @Override
  public void fetchAndRun(JThread thread) {
    var stack = thread.top().stack();
    stack.pushInt((int) stack.popDouble());
  }

}
