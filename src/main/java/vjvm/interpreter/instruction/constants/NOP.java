package vjvm.interpreter.instruction.constants;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

public class NOP extends Instruction {

  @Override
  public void fetchAndRun(JThread thread) {
    // do nothing
  }

}
