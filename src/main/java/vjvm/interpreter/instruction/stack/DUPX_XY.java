package vjvm.interpreter.instruction.stack;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.var;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.OperandStack;
import vjvm.runtime.ProgramCounter;
import vjvm.runtime.classdata.MethodInfo;

import java.util.function.BiConsumer;
import java.util.function.Function;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DUPX_XY<T, U> extends Instruction {
  private final Function<OperandStack, T> popFunc1;
  private final Function<OperandStack, U> popFunc2;
  private final BiConsumer<OperandStack, T> pushFunc1;
  private final BiConsumer<OperandStack, U> pushFunc2;
  private final String name;

  public static DUPX_XY<Long, Integer> DUP2_X1(ProgramCounter pc, MethodInfo method) {
    return new DUPX_XY<>(OperandStack::popLong, OperandStack::popInt, OperandStack::pushLong, OperandStack::pushInt,
      "dup2_x1");
  }

  public static DUPX_XY<Long, Long> DUP2_X2(ProgramCounter pc, MethodInfo method) {
    return new DUPX_XY<>(OperandStack::popLong, OperandStack::popLong, OperandStack::pushLong, OperandStack::pushLong,
      "dup2_x2");
  }

  public static DUPX_XY<Integer, Integer> DUP_X1(ProgramCounter pc, MethodInfo method) {
    return new DUPX_XY<>(OperandStack::popInt, OperandStack::popInt, OperandStack::pushInt, OperandStack::pushInt,
      "dup_x1");
  }

  public static DUPX_XY<Integer, Long> DUP_X2(ProgramCounter pc, MethodInfo method) {
    return new DUPX_XY<>(OperandStack::popInt, OperandStack::popLong, OperandStack::pushInt, OperandStack::pushLong,
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
