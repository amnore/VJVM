package vjvm.interpreter.instruction.control;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.var;
import vjvm.interpreter.instruction.Instruction;
import vjvm.interpreter.instruction.conversions.X2Y;
import vjvm.runtime.JThread;
import vjvm.runtime.OperandStack;
import vjvm.runtime.ProgramCounter;
import vjvm.runtime.classdata.MethodInfo;

import java.util.function.BiConsumer;
import java.util.function.Function;

import static vjvm.classfiledefs.Descriptors.*;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class XRETURN<T> extends Instruction {
  private final Function<OperandStack, T> popFunc;
  private final Function<T, T> convertFunc;
  private final BiConsumer<OperandStack, T> pushFunc;
  private final String name;

  public static XRETURN<Void> RETURN(ProgramCounter pc, MethodInfo method) {
    return new XRETURN<Void>(s -> null, Function.identity(), (s, v) -> {
    }, "return");
  }

  public static XRETURN<Integer> IRETURN(ProgramCounter pc, MethodInfo method) {
    var type = method.descriptor().charAt(0);
    Function<Integer, Integer> convertFunc;
    switch (type) {
      case DESC_boolean:
        convertFunc = x -> x & 1;
        break;
      case DESC_byte:
        convertFunc = X2Y::i2b;
        break;
      case DESC_char:
        convertFunc = X2Y::i2c;
        break;
      case DESC_short:
        convertFunc = X2Y::i2s;
        break;
      case DESC_int:
        convertFunc = Function.identity();
        break;
      default:
        throw new Error(String.format("invalid return type %s", type));
    }

    return new XRETURN<>(OperandStack::popInt, convertFunc, OperandStack::pushInt, "ireturn");
  }

  public static XRETURN<Integer> ARETURN(ProgramCounter pc, MethodInfo method) {
    return new XRETURN<>(OperandStack::popInt, Function.identity(), OperandStack::pushInt, "areturn");
  }

  public static XRETURN<Long> LRETURN(ProgramCounter pc, MethodInfo method) {
    return new XRETURN<>(OperandStack::popLong, Function.identity(), OperandStack::pushLong, "lreturn");
  }

  public static XRETURN<Float> FRETURN(ProgramCounter pc, MethodInfo method) {
    return new XRETURN<>(OperandStack::popFloat, Function.identity(), OperandStack::pushFloat, "freturn");
  }

  public static XRETURN<Double> DRETURN(ProgramCounter pc, MethodInfo method) {
    return new XRETURN<Double>(OperandStack::popDouble, Function.identity(), OperandStack::pushDouble, "dreturn");
  }

  @Override
  public void run(JThread thread) {
    var v = popFunc.apply(thread.top().stack());
    thread.pop();
    pushFunc.accept(thread.top().stack(), convertFunc.apply(v));
  }

  @Override
  public String toString() {
    return name;
  }
}
