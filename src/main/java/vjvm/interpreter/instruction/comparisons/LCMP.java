package vjvm.interpreter.instruction.comparisons;

import lombok.var;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.ProgramCounter;
import vjvm.runtime.classdata.MethodInfo;

public class LCMP extends Instruction {
  public LCMP(ProgramCounter pc, MethodInfo method) {
  }

  @Override
  public void run(JThread thread) {
    var stack = thread.top().stack();
    var right = stack.popLong();
    var left = stack.popLong();
    stack.pushInt(Long.compare(left, right));
  }

  @Override
  public String toString() {
    return "lcmp";
  }
}
