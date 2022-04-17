package vjvm.interpreter.instruction.stores;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.var;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.OperandStack;
import vjvm.runtime.ProgramCounter;
import vjvm.runtime.classdata.MethodInfo;
import vjvm.runtime.object.ArrayObject;
import vjvm.utils.TriConsumer;

import java.util.function.Function;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class XASTORE<T> extends Instruction {
  private final Function<OperandStack, T> popFunc;
  private final TriConsumer<ArrayObject, Integer, T> putFunc;
  private final String name;

  public static XASTORE<Integer> AASTORE(ProgramCounter pc, MethodInfo method) {
    return new XASTORE<>(OperandStack::popAddress, ArrayObject::address, "aastore");
  }

  public static XASTORE<Integer> IASTORE(ProgramCounter pc, MethodInfo method) {
    return new XASTORE<>(OperandStack::popInt, ArrayObject::int_, "iastore");
  }

  public static XASTORE<Float> FASTORE(ProgramCounter pc, MethodInfo method) {
    return new XASTORE<>(OperandStack::popFloat, ArrayObject::float_, "fastore");
  }

  public static XASTORE<Byte> BASTORE(ProgramCounter pc, MethodInfo method) {
    return new XASTORE<>(OperandStack::popByte, ArrayObject::byte_, "bastore");
  }

  public static XASTORE<Character> CASTORE(ProgramCounter pc, MethodInfo method) {
    return new XASTORE<>(OperandStack::popChar, ArrayObject::char_, "castore");
  }

  public static XASTORE<Short> SASTORE(ProgramCounter pc, MethodInfo method) {
    return new XASTORE<>(OperandStack::popShort, ArrayObject::short_, "sastore");
  }

  public static XASTORE<Long> LASTORE(ProgramCounter pc, MethodInfo method) {
    return new XASTORE<>(OperandStack::popLong, ArrayObject::long_, "lastore");
  }

  public static XASTORE<Double> DASTORE(ProgramCounter pc, MethodInfo method) {
    return new XASTORE<>(OperandStack::popDouble, ArrayObject::double_, "dastore");
  }

  @Override
  public void run(JThread thread) {
    var stack = thread.top().stack();
    var value = popFunc.apply(stack);
    var index = stack.popInt();
    var obj = thread.context().heap().get(stack.popAddress());

    assert obj.type().array();
    putFunc.accept(((ArrayObject) obj), index, value);
  }

  @Override
  public String toString() {
    return name;
  }
}
