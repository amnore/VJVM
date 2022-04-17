package vjvm.interpreter.instruction.math;

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
import java.util.function.UnaryOperator;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class XNEG<T> extends Instruction {
  private final Function<OperandStack, T> popFunc;
  private final UnaryOperator<T> negFunc;
  private final BiConsumer<OperandStack, T> pushFunc;
  private final String name;

  public static XNEG<Double> DNEG(ProgramCounter pc, MethodInfo method) {
    return new XNEG<>(OperandStack::popDouble, x -> -x, OperandStack::pushDouble, "dneg");
  }

  public static XNEG<Float> FNEG(ProgramCounter pc, MethodInfo method) {
    return new XNEG<>(OperandStack::popFloat, x -> -x, OperandStack::pushFloat, "fneg");
  }

  public static XNEG<Integer> INEG(ProgramCounter pc, MethodInfo method) {
    return new XNEG<>(OperandStack::popInt, x -> -x, OperandStack::pushInt, "ineg");
  }

  public static XNEG<Long> LNEG(ProgramCounter pc, MethodInfo method) {
    return new XNEG<>(OperandStack::popLong, x -> -x, OperandStack::pushLong, "lneg");
  }

  @Override
  public void run(JThread thread) {
    var stack = thread.top().stack();
    var value = popFunc.apply(stack);
    pushFunc.accept(stack, negFunc.apply(value));
  }

  @Override
  public String toString() {
    return name;
  }
}
