package vjvm.interpreter.instruction.stack;

import lombok.AllArgsConstructor;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.OperandStack;
import vjvm.runtime.ProgramCounter;
import vjvm.runtime.Slots;
import vjvm.runtime.classdata.MethodInfo;

import java.util.function.Function;

@AllArgsConstructor
public class POPX extends Instruction {
  private final Function<OperandStack, Slots> popFunc;
  private final String name;

  public static POPX POP(ProgramCounter pc, MethodInfo method) {
    return new POPX(s -> s.popSlots(1), "pop");
  }

  public static POPX POP2(ProgramCounter pc, MethodInfo method) {
    return new POPX(s -> s.popSlots(2), "pop2");
  }

  @Override
  public void run(JThread thread) {
    popFunc.apply(thread.top().stack());
  }

  @Override
  public String toString() {
    return name;
  }
}
