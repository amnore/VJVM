package vjvm.interpreter.instruction.math;

import lombok.var;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

public class DNEG extends Instruction {

  @Override
  public void fetchAndRun(JThread thread) {
    var stack = thread.top().stack();
    stack.pushDouble(-stack.popDouble());
  }

}
