package vjvm.interpreter.instruction;

import lombok.var;
import vjvm.runtime.JThread;

public abstract class Instruction {
  public abstract void fetchAndRun(JThread thread);
}
