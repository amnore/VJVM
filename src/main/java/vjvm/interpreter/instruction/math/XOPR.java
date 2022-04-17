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
import java.util.function.BinaryOperator;
import java.util.function.Function;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class XOPR<T> extends Instruction {
  private final Function<OperandStack, T> popFunc;
  private final BinaryOperator<T> opr;
  private final BiConsumer<OperandStack, T> pushFunc;
  private final String name;

  public static XOPR<Integer> IADD(ProgramCounter pc, MethodInfo method) {
    return new XOPR<>(OperandStack::popInt, (x, y) -> x + y, OperandStack::pushInt, "iadd");
  }

  public static XOPR<Integer> ISUB(ProgramCounter pc, MethodInfo method) {
    return new XOPR<>(OperandStack::popInt, (x, y) -> x - y, OperandStack::pushInt, "isub");
  }

  public static XOPR<Integer> IMUL(ProgramCounter pc, MethodInfo method) {
    return new XOPR<>(OperandStack::popInt, (x, y) -> x * y, OperandStack::pushInt, "imul");
  }

  public static XOPR<Integer> IDIV(ProgramCounter pc, MethodInfo method) {
    return new XOPR<>(OperandStack::popInt, (x, y) -> x / y, OperandStack::pushInt, "idiv");
  }

  public static XOPR<Integer> IREM(ProgramCounter pc, MethodInfo method) {
    return new XOPR<>(OperandStack::popInt, (x, y) -> x % y, OperandStack::pushInt, "irem");
  }

  public static XOPR<Float> FADD(ProgramCounter pc, MethodInfo method) {
    return new XOPR<>(OperandStack::popFloat, (x, y) -> x + y, OperandStack::pushFloat, "fadd");
  }

  public static XOPR<Float> FSUB(ProgramCounter pc, MethodInfo method) {
    return new XOPR<>(OperandStack::popFloat, (x, y) -> x - y, OperandStack::pushFloat, "fsub");
  }

  public static XOPR<Float> FMUL(ProgramCounter pc, MethodInfo method) {
    return new XOPR<>(OperandStack::popFloat, (x, y) -> x * y, OperandStack::pushFloat, "fmul");
  }

  public static XOPR<Float> FDIV(ProgramCounter pc, MethodInfo method) {
    return new XOPR<>(OperandStack::popFloat, (x, y) -> x / y, OperandStack::pushFloat, "fdiv");
  }

  public static XOPR<Float> FREM(ProgramCounter pc, MethodInfo method) {
    return new XOPR<>(OperandStack::popFloat, (x, y) -> x % y, OperandStack::pushFloat, "frem");
  }

  public static XOPR<Long> LADD(ProgramCounter pc, MethodInfo method) {
    return new XOPR<>(OperandStack::popLong, (x, y) -> x + y, OperandStack::pushLong, "ladd");
  }

  public static XOPR<Long> LSUB(ProgramCounter pc, MethodInfo method) {
    return new XOPR<>(OperandStack::popLong, (x, y) -> x - y, OperandStack::pushLong, "lsub");
  }

  public static XOPR<Long> LMUL(ProgramCounter pc, MethodInfo method) {
    return new XOPR<>(OperandStack::popLong, (x, y) -> x * y, OperandStack::pushLong, "lmul");
  }

  public static XOPR<Long> LDIV(ProgramCounter pc, MethodInfo method) {
    return new XOPR<>(OperandStack::popLong, (x, y) -> x / y, OperandStack::pushLong, "ldiv");
  }

  public static XOPR<Long> LREM(ProgramCounter pc, MethodInfo method) {
    return new XOPR<>(OperandStack::popLong, (x, y) -> x % y, OperandStack::pushLong, "lrem");
  }

  public static XOPR<Double> DADD(ProgramCounter pc, MethodInfo method) {
    return new XOPR<>(OperandStack::popDouble, (x, y) -> x + y, OperandStack::pushDouble, "dadd");
  }

  public static XOPR<Double> DSUB(ProgramCounter pc, MethodInfo method) {
    return new XOPR<>(OperandStack::popDouble, (x, y) -> x - y, OperandStack::pushDouble, "dsub");
  }

  public static XOPR<Double> DMUL(ProgramCounter pc, MethodInfo method) {
    return new XOPR<>(OperandStack::popDouble, (x, y) -> x * y, OperandStack::pushDouble, "dmul");
  }

  public static XOPR<Double> DDIV(ProgramCounter pc, MethodInfo method) {
    return new XOPR<>(OperandStack::popDouble, (x, y) -> x / y, OperandStack::pushDouble, "ddiv");
  }

  public static XOPR<Double> DREM(ProgramCounter pc, MethodInfo method) {
    return new XOPR<>(OperandStack::popDouble, (x, y) -> x % y, OperandStack::pushDouble, "drem");
  }

  public static XOPR<Integer> ISHL(ProgramCounter pc, MethodInfo method) {
    return new XOPR<>(OperandStack::popInt, (x, y) -> x << y, OperandStack::pushInt, "ishl");
  }

  public static XOPR<Integer> ISHR(ProgramCounter pc, MethodInfo method) {
    return new XOPR<>(OperandStack::popInt, (x, y) -> x >> y, OperandStack::pushInt, "ishr");
  }

  public static XOPR<Integer> IUSHR(ProgramCounter pc, MethodInfo method) {
    return new XOPR<>(OperandStack::popInt, (x, y) -> x >>> y, OperandStack::pushInt, "iushr");
  }

  public static XOPR<Integer> IAND(ProgramCounter pc, MethodInfo method) {
    return new XOPR<>(OperandStack::popInt, (x, y) -> x & y, OperandStack::pushInt, "iand");
  }

  public static XOPR<Integer> IOR(ProgramCounter pc, MethodInfo method) {
    return new XOPR<>(OperandStack::popInt, (x, y) -> x | y, OperandStack::pushInt, "ior");
  }

  public static XOPR<Integer> IXOR(ProgramCounter pc, MethodInfo method) {
    return new XOPR<>(OperandStack::popInt, (x, y) -> x ^ y, OperandStack::pushInt, "ixor");
  }

  public static XOPR<Long> LAND(ProgramCounter pc, MethodInfo method) {
    return new XOPR<>(OperandStack::popLong, (x, y) -> x & y, OperandStack::pushLong, "land");
  }

  public static XOPR<Long> LOR(ProgramCounter pc, MethodInfo method) {
    return new XOPR<>(OperandStack::popLong, (x, y) -> x | y, OperandStack::pushLong, "lor");
  }

  public static XOPR<Long> LXOR(ProgramCounter pc, MethodInfo method) {
    return new XOPR<>(OperandStack::popLong, (x, y) -> x ^ y, OperandStack::pushLong, "lxor");
  }

  @Override
  public void run(JThread thread) {
    var stack = thread.top().stack();
    var right = popFunc.apply(stack);
    var left = popFunc.apply(stack);
    pushFunc.accept(stack, opr.apply(left, right));
  }

  @Override
  public String toString() {
    return name;
  }
}
