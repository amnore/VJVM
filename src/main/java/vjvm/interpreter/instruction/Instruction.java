package vjvm.interpreter.instruction;

import vjvm.runtime.JThread;

public abstract class Instruction {
  public abstract void run(JThread thread);

  @Override
  public abstract String toString();
}
