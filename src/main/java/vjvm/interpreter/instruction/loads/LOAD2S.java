package vjvm.interpreter.instruction.loads;

import lombok.var;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

public class LOAD2S extends Instruction {

  @Override
  public void fetchAndRun(JThread thread) {
    var frame = thread.top();
    frame.stack().pushLong(frame.vars().long_(thread.pc().ubyte()));
  }

}
