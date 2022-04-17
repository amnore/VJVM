package vjvm.interpreter.instruction.conversions;

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
public class X2Y<T, U> extends Instruction {
  private final Function<OperandStack, T> popFunc;
  private final Function<T, U> convertFunc;
  private final BiConsumer<OperandStack, U> pushFunc;
  private final String name;

  public static X2Y<Integer, Long> I2L(ProgramCounter pc, MethodInfo method) {
    return new X2Y<>(OperandStack::popInt, Integer::longValue, OperandStack::pushLong, "i2l");
  }

  public static X2Y<Integer, Float> I2F(ProgramCounter pc, MethodInfo method) {
    return new X2Y<>(OperandStack::popInt, Integer::floatValue, OperandStack::pushFloat, "i2f");
  }

  public static X2Y<Integer, Double> I2D(ProgramCounter pc, MethodInfo method) {
    return new X2Y<>(OperandStack::popInt, Integer::doubleValue, OperandStack::pushDouble, "i2d");
  }

  public static X2Y<Long, Integer> L2I(ProgramCounter pc, MethodInfo method) {
    return new X2Y<>(OperandStack::popLong, Long::intValue, OperandStack::pushInt, "l2i");
  }

  public static X2Y<Long, Float> L2F(ProgramCounter pc, MethodInfo method) {
    return new X2Y<>(OperandStack::popLong, Long::floatValue, OperandStack::pushFloat, "l2f");
  }

  public static X2Y<Long, Double> L2D(ProgramCounter pc, MethodInfo method) {
    return new X2Y<>(OperandStack::popLong, Long::doubleValue, OperandStack::pushDouble, "l2d");
  }

  public static X2Y<Float, Integer> F2I(ProgramCounter pc, MethodInfo method) {
    return new X2Y<>(OperandStack::popFloat, Float::intValue, OperandStack::pushInt, "f2i");
  }

  public static X2Y<Float, Long> F2L(ProgramCounter pc, MethodInfo method) {
    return new X2Y<>(OperandStack::popFloat, Float::longValue, OperandStack::pushLong, "f2l");
  }

  public static X2Y<Float, Double> F2D(ProgramCounter pc, MethodInfo method) {
    return new X2Y<>(OperandStack::popFloat, Float::doubleValue, OperandStack::pushDouble, "f2d");
  }

  public static X2Y<Double, Integer> D2I(ProgramCounter pc, MethodInfo method) {
    return new X2Y<>(OperandStack::popDouble, Double::intValue, OperandStack::pushInt, "d2i");
  }

  public static X2Y<Double, Long> D2L(ProgramCounter pc, MethodInfo method) {
    return new X2Y<>(OperandStack::popDouble, Double::longValue, OperandStack::pushLong, "d2l");
  }

  public static X2Y<Double, Float> D2F(ProgramCounter pc, MethodInfo method) {
    return new X2Y<>(OperandStack::popDouble, Double::floatValue, OperandStack::pushFloat, "d2f");
  }

  public static X2Y<Integer, Integer> I2B(ProgramCounter pc, MethodInfo method) {
    return new X2Y<>(OperandStack::popInt, X2Y::i2b, OperandStack::pushInt, "i2b");
  }

  public static X2Y<Integer, Integer> I2C(ProgramCounter pc, MethodInfo method) {
    return new X2Y<>(OperandStack::popInt, X2Y::i2c, OperandStack::pushInt, "i2c");
  }

  public static X2Y<Integer, Integer> I2S(ProgramCounter pc, MethodInfo method) {
    return new X2Y<>(OperandStack::popInt, X2Y::i2s, OperandStack::pushInt, "i2s");
  }

  public static int i2b(int value) {
    return (byte) value;
  }

  public static int i2c(int value) {
    return (char) value;
  }

  public static int i2s(int value) {
    return (short) value;
  }

  public static int c2i(char value) {
    return value;
  }

  @Override
  public void run(JThread thread) {
    var stack = thread.top().stack();
    var t = popFunc.apply(stack);
    var u = convertFunc.apply(t);
    pushFunc.accept(stack, u);
  }

  @Override
  public String toString() {
    return name;
  }
}
