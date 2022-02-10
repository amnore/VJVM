package vjvm.interpreter.instruction.comparisons;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

public class LCMP extends Instruction {

  @Override
  public void fetchAndRun(JThread thread) {
    var stack = thread.top().stack();
    var right = stack.popLong();
    var left = stack.popLong();
    stack.pushInt(Long.compare(left, right));
  }

}
