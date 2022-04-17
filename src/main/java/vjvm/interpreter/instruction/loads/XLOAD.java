package vjvm.interpreter.instruction.loads;

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
import java.util.function.BiFunction;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class XLOAD<T> extends Instruction {
  private final BiFunction<Slots, Integer, T> getFunc;
  private final BiConsumer<OperandStack, T> pushFunc;
  private final int index;
  private final String name;

  public static XLOAD<Integer> ILOAD(ProgramCounter pc, MethodInfo method) {
    return new XLOAD<>(Slots::int_, OperandStack::pushInt, pc.ubyte(), "iload");
  }

  public static XLOAD<Integer> ILOAD_0(ProgramCounter pc, MethodInfo method) {
    return new XLOAD<>(Slots::int_, OperandStack::pushInt, 0, "iload");
  }

  public static XLOAD<Integer> ILOAD_1(ProgramCounter pc, MethodInfo method) {
    return new XLOAD<>(Slots::int_, OperandStack::pushInt, 1, "iload");
  }

  public static XLOAD<Integer> ILOAD_2(ProgramCounter pc, MethodInfo method) {
    return new XLOAD<>(Slots::int_, OperandStack::pushInt, 2, "iload");
  }

  public static XLOAD<Integer> ILOAD_3(ProgramCounter pc, MethodInfo method) {
    return new XLOAD<>(Slots::int_, OperandStack::pushInt, 3, "iload");
  }

  public static XLOAD<Long> LLOAD(ProgramCounter pc, MethodInfo method) {
    return new XLOAD<>(Slots::long_, OperandStack::pushLong, pc.ubyte(), "lload");
  }

  public static XLOAD<Long> LLOAD_0(ProgramCounter pc, MethodInfo method) {
    return new XLOAD<>(Slots::long_, OperandStack::pushLong, 0, "lload");
  }

  public static XLOAD<Long> LLOAD_1(ProgramCounter pc, MethodInfo method) {
    return new XLOAD<>(Slots::long_, OperandStack::pushLong, 1, "lload");
  }

  public static XLOAD<Long> LLOAD_2(ProgramCounter pc, MethodInfo method) {
    return new XLOAD<>(Slots::long_, OperandStack::pushLong, 2, "lload");
  }

  public static XLOAD<Long> LLOAD_3(ProgramCounter pc, MethodInfo method) {
    return new XLOAD<>(Slots::long_, OperandStack::pushLong, 3, "lload");
  }

  public static XLOAD<Float> FLOAD(ProgramCounter pc, MethodInfo method) {
    return new XLOAD<>(Slots::float_, OperandStack::pushFloat, pc.ubyte(), "fload");
  }

  public static XLOAD<Float> FLOAD_0(ProgramCounter pc, MethodInfo method) {
    return new XLOAD<>(Slots::float_, OperandStack::pushFloat, 0, "fload");
  }

  public static XLOAD<Float> FLOAD_1(ProgramCounter pc, MethodInfo method) {
    return new XLOAD<>(Slots::float_, OperandStack::pushFloat, 1, "fload");
  }

  public static XLOAD<Float> FLOAD_2(ProgramCounter pc, MethodInfo method) {
    return new XLOAD<>(Slots::float_, OperandStack::pushFloat, 2, "fload");
  }

  public static XLOAD<Float> FLOAD_3(ProgramCounter pc, MethodInfo method) {
    return new XLOAD<>(Slots::float_, OperandStack::pushFloat, 3, "fload");
  }

  public static XLOAD<Double> DLOAD(ProgramCounter pc, MethodInfo method) {
    return new XLOAD<>(Slots::double_, OperandStack::pushDouble, pc.ubyte(), "dload");
  }

  public static XLOAD<Double> DLOAD_0(ProgramCounter pc, MethodInfo method) {
    return new XLOAD<>(Slots::double_, OperandStack::pushDouble, 0, "dload");
  }

  public static XLOAD<Double> DLOAD_1(ProgramCounter pc, MethodInfo method) {
    return new XLOAD<>(Slots::double_, OperandStack::pushDouble, 1, "dload");
  }

  public static XLOAD<Double> DLOAD_2(ProgramCounter pc, MethodInfo method) {
    return new XLOAD<>(Slots::double_, OperandStack::pushDouble, 2, "dload");
  }

  public static XLOAD<Double> DLOAD_3(ProgramCounter pc, MethodInfo method) {
    return new XLOAD<>(Slots::double_, OperandStack::pushDouble, 3, "dload");
  }

  public static XLOAD<Integer> ALOAD(ProgramCounter pc, MethodInfo method) {
    return new XLOAD<>(Slots::address, OperandStack::pushAddress, pc.ubyte(), "aload");
  }

  public static XLOAD<Integer> ALOAD_0(ProgramCounter pc, MethodInfo method) {
    return new XLOAD<>(Slots::address, OperandStack::pushAddress, 0, "aload");
  }

  public static XLOAD<Integer> ALOAD_1(ProgramCounter pc, MethodInfo method) {
    return new XLOAD<>(Slots::address, OperandStack::pushAddress, 1, "aload");
  }

  public static XLOAD<Integer> ALOAD_2(ProgramCounter pc, MethodInfo method) {
    return new XLOAD<>(Slots::address, OperandStack::pushAddress, 2, "aload");
  }

  public static XLOAD<Integer> ALOAD_3(ProgramCounter pc, MethodInfo method) {
    return new XLOAD<>(Slots::address, OperandStack::pushAddress, 3, "aload");
  }

  @Override
  public void run(JThread thread) {
    var frame = thread.top();
    var var = getFunc.apply(frame.vars(), index);
    pushFunc.accept(frame.stack(), var);
  }

  @Override
  public String toString() {
    return String.format("%s %d", name, index);
  }
}
