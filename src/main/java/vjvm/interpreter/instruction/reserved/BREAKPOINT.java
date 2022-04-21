package vjvm.interpreter.instruction.reserved;

import lombok.var;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.ProgramCounter;
import vjvm.runtime.classdata.MethodInfo;

public class BREAKPOINT extends Instruction {
  public BREAKPOINT(ProgramCounter pc, MethodInfo method) {
  }

  @Override
  public void run(JThread thread) {
    var pc = thread.pc();
    pc.position(pc.position() - 1);
    thread.context().interpreter().break_();
  }

  @Override
  public String toString() {
    return "breakpoint";
  }
}
