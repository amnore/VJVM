package vjvm.interpreter.instruction.comparisons;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

public class DCMPG extends Instruction {

  @Override
  public void fetchAndRun(JThread thread) {
    var stack = thread.top().stack();
    var right = stack.popDouble();
    var left = stack.popDouble();
    stack.pushInt(Double.compare(left, right));
  }

}