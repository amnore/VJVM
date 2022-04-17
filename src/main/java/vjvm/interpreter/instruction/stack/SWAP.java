package vjvm.interpreter.instruction.stack;

import lombok.var;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.ProgramCounter;
import vjvm.runtime.classdata.MethodInfo;

public class SWAP extends Instruction {
  public SWAP(ProgramCounter pc, MethodInfo method) {
  }

  @Override
  public void run(JThread thread) {
    var stack = thread.top().stack();
    var value = stack.popInt();
    var value2 = stack.popInt();
    stack.pushInt(value);
    stack.pushInt(value2);
  }

  @Override
  public String toString() {
    return "swap";
  }
}
