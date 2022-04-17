package vjvm.interpreter.instruction.constants;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.var;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.ProgramCounter;
import vjvm.runtime.classdata.MethodInfo;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class XPUSH extends Instruction {
  private final int value;
  private final String name;

  public static XPUSH BIPUSH(ProgramCounter pc, MethodInfo method) {
    return new XPUSH(pc.byte_(), "bipush");
  }

  public static XPUSH SIPUSH(ProgramCounter pc, MethodInfo method) {
    return new XPUSH(pc.short_(), "sipush");
  }

  @Override
  public void run(JThread thread) {
    var opStack = thread.top().stack();
    opStack.pushInt(value);
  }

  @Override
  public String toString() {
    return String.format("%s %d", name, value);
  }
}
