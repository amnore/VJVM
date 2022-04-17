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
public class DUPX<T> extends Instruction {
  private final Function<OperandStack, T> popFunc;
  private final BiConsumer<OperandStack, T> pushFunc;
  private final String name;

  public static DUPX<Integer> DUP(ProgramCounter pc, MethodInfo method) {
    return new DUPX<>(OperandStack::popInt, OperandStack::pushInt, "dup");
  }

  public static DUPX<Long> DUP2(ProgramCounter pc, MethodInfo method) {
    return new DUPX<>(OperandStack::popLong, OperandStack::pushLong, "dup2");
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
