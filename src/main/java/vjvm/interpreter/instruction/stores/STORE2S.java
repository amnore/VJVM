package vjvm.interpreter.instruction.stores;

import lombok.var;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

public class STORE2S extends Instruction {

  @Override
  public void fetchAndRun(JThread thread) {
    var frame = thread.top();
    frame.vars().long_(thread.pc().ubyte(), frame.stack().popLong());
  }

}
