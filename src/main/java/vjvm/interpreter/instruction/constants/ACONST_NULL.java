package vjvm.interpreter.instruction.constants;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

public class ACONST_NULL extends Instruction {

  @Override
  public void fetchAndRun(JThread thread) {
    var opStack = thread.top().stack();
    opStack.pushAddress(0);
  }

}
