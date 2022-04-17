package vjvm.interpreter.instruction.stack;

import lombok.AllArgsConstructor;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.OperandStack;
import vjvm.runtime.ProgramCounter;
import vjvm.runtime.classdata.MethodInfo;

import java.util.function.Function;

@AllArgsConstructor
public class POPX<T> extends Instruction {
  private final Function<OperandStack, T> popFunc;
  private final String name;

  public static POPX<Integer> POP(ProgramCounter pc, MethodInfo method) {
    return new POPX<>(OperandStack::popInt, "pop");
  }

  public static POPX<Long> POP2(ProgramCounter pc, MethodInfo method) {
    return new POPX<>(OperandStack::popLong, "pop2");
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
