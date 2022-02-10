package vjvm.interpreter.instruction.stack;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

public class POP extends Instruction {
  @Override
  public void fetchAndRun(JThread thread) {
    thread.top().stack().popInt();
  }

}
