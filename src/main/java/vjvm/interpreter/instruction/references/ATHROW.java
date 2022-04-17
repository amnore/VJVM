package vjvm.interpreter.instruction.references;

import lombok.var;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.ProgramCounter;
import vjvm.runtime.classdata.MethodInfo;

public class ATHROW extends Instruction {
  public ATHROW(ProgramCounter pc, MethodInfo method) {
  }

  @Override
  public void run(JThread thread) {
    var ctx = thread.context();
    var addr = thread.top().stack().popAddress();

    assert addr != 0;
    thread.exception(ctx.heap().get(addr));
  }

  @Override
  public String toString() {
    return "athrow";
  }
}
