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
public class DUPX_XY extends Instruction {
  private final Function<OperandStack, Slots> popFunc1;
  private final Function<OperandStack, Slots> popFunc2;
  private final BiConsumer<OperandStack, Slots> pushFunc1;
  private final BiConsumer<OperandStack, Slots> pushFunc2;
  private final String name;

  public static DUPX_XY DUP2_X1(ProgramCounter pc, MethodInfo method) {
    return new DUPX_XY(s -> s.popSlots(2), s -> s.popSlots(1), (s, t) -> s.pushSlots(t), (s, t) -> s.pushSlots(t),
        "dup2_x1");
  }

  public static DUPX_XY DUP2_X2(ProgramCounter pc, MethodInfo method) {
    return new DUPX_XY(s -> s.popSlots(2), s -> s.popSlots(2), (s, t) -> s.pushSlots(t), (s, t) -> s.pushSlots(t),
        "dup2_x2");
  }

  public static DUPX_XY DUP_X1(ProgramCounter pc, MethodInfo method) {
    return new DUPX_XY(s -> s.popSlots(1), s -> s.popSlots(1), (s, t) -> s.pushSlots(t), (s, t) -> s.pushSlots(t),
        "dup_x1");
  }

  public static DUPX_XY DUP_X2(ProgramCounter pc, MethodInfo method) {
    return new DUPX_XY(s -> s.popSlots(1), s -> s.popSlots(2), (s, t) -> s.pushSlots(t), (s, t) -> s.pushSlots(t),
        "dup_x2");
  }

  @Override
  public void run(JThread thread) {
    var stack = thread.top().stack();
    var value1 = popFunc1.apply(stack);
    var value2 = popFunc2.apply(stack);
    pushFunc1.accept(stack, value1);
    pushFunc2.accept(stack, value2);
    pushFunc1.accept(stack, value1);
  }

  @Override
  public String toString() {
    return name;
  }
}
