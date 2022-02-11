package vjvm.interpreter.instruction.conversions;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

public class I2C extends Instruction {

  @Override
  public void fetchAndRun(JThread thread) {
    var stack = thread.top().stack();
    stack.pushInt((char) stack.popInt());
  }

}