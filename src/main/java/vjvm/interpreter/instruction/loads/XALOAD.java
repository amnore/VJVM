package vjvm.interpreter.instruction.loads;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.var;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.OperandStack;
import vjvm.runtime.ProgramCounter;
import vjvm.runtime.classdata.MethodInfo;
import vjvm.runtime.object.ArrayObject;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class XALOAD<T> extends Instruction {
  private final BiFunction<ArrayObject, Integer, T> getFunc;
  private final BiConsumer<OperandStack, T> pushFunc;
  private final String name;

  public static XALOAD<Integer> AALOAD(ProgramCounter pc, MethodInfo method) {
    return new XALOAD<>(ArrayObject::address, OperandStack::pushAddress, "aaload");
  }

  public static XALOAD<Integer> IALOAD(ProgramCounter pc, MethodInfo method) {
    return new XALOAD<>(ArrayObject::int_, OperandStack::pushInt, "iaload");
  }

  public static XALOAD<Float> FALOAD(ProgramCounter pc, MethodInfo method) {
    return new XALOAD<>(ArrayObject::float_, OperandStack::pushFloat, "faload");
  }

  public static XALOAD<Byte> BALOAD(ProgramCounter pc, MethodInfo method) {
    return new XALOAD<>(ArrayObject::byte_, OperandStack::pushByte, "baload");
  }

  public static XALOAD<Character> CALOAD(ProgramCounter pc, MethodInfo method) {
    return new XALOAD<>(ArrayObject::char_, OperandStack::pushChar, "caload");
  }

  public static XALOAD<Short> SALOAD(ProgramCounter pc, MethodInfo method) {
    return new XALOAD<>(ArrayObject::short_, OperandStack::pushShort, "saload");
  }

  public static XALOAD<Long> LALOAD(ProgramCounter pc, MethodInfo method) {
    return new XALOAD<>(ArrayObject::long_, OperandStack::pushLong, "laload");
  }

  public static XALOAD<Double> DALOAD(ProgramCounter pc, MethodInfo method) {
    return new XALOAD<>(ArrayObject::double_, OperandStack::pushDouble, "daload");
  }

  @Override
  public void run(JThread thread) {
    var stack = thread.top().stack();
    var index = stack.popInt();
    var obj = thread.context().heap().get(stack.popAddress());

    assert obj.type().array();
    var value = getFunc.apply(((ArrayObject) obj), index);
    pushFunc.accept(stack, value);
  }

  @Override
  public String toString() {
    return name;
  }
}
