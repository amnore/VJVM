package vjvm.interpreter.instruction.control;

import lombok.var;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

public class RETURN2S extends Instruction {

  @Override
  public void fetchAndRun(JThread thread) {
    var ret = thread.top().stack().popLong();
    thread.pop();
    thread.top().stack().pushLong(ret);
  }

}
