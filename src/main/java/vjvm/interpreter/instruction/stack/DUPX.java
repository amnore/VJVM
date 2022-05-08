package vjvm.interpreter.instruction.stack;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.var;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.OperandStack;
import vjvm.runtime.ProgramCounter;
import vjvm.runtime.Slots;
import vjvm.runtime.classdata.MethodInfo;

import java.util.function.BiConsumer;
import java.util.function.Function;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DUPX extends Instruction {
  private final Function<OperandStack, Slots> popFunc;
  private final BiConsumer<OperandStack, Slots> pushFunc;
  private final String name;

  public static DUPX DUP(ProgramCounter pc, MethodInfo method) {
    return new DUPX(s -> s.popSlots(1), (s, t) -> s.pushSlots(t), "dup");
  }

  public static DUPX DUP2(ProgramCounter pc, MethodInfo method) {
    return new DUPX(s -> s.popSlots(2), (s, t) -> s.pushSlots(t), "dup2");
  }

  @Override
  public void run(JThread thread) {
    var stack = thread.top().stack();
    var value = popFunc.apply(stack);
    pushFunc.accept(stack, value);
    pushFunc.accept(stack, value);
  }

  @Override
  public String toString() {
    return name;
  }
}
