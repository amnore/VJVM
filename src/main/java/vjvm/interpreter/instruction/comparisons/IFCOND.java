package vjvm.interpreter.instruction.comparisons;

import lombok.var;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.OperandStack;
import vjvm.runtime.ProgramCounter;
import vjvm.runtime.classdata.MethodInfo;

import java.util.function.Function;
import java.util.function.Predicate;

public class IFCOND<T> extends Instruction {
  private final Function<OperandStack, T> popFunc;
  private final Predicate<T> pred;
  private final String name;
  private final int offset;

  private IFCOND(Function<OperandStack, T> popFunc, Predicate<T> pred, String name, ProgramCounter pc) {
    this.popFunc = popFunc;
    this.pred = pred;
    this.name = name;
    this.offset = pc.short_() - 3;
  }

  public static IFCOND<Integer> IFEQ(ProgramCounter pc, MethodInfo method) {
    return new IFCOND<>(OperandStack::popInt, x -> x == 0, "ifeq", pc);
  }

  public static IFCOND<Integer> IFNE(ProgramCounter pc, MethodInfo method) {
    return new IFCOND<>(OperandStack::popInt, x -> x != 0, "ifne", pc);
  }

  public static IFCOND<Integer> IFLT(ProgramCounter pc, MethodInfo method) {
    return new IFCOND<>(OperandStack::popInt, x -> x < 0, "iflt", pc);
  }

  public static IFCOND<Integer> IFLE(ProgramCounter pc, MethodInfo method) {
    return new IFCOND<>(OperandStack::popInt, x -> x <= 0, "ifle", pc);
  }

  public static IFCOND<Integer> IFGE(ProgramCounter pc, MethodInfo method) {
    return new IFCOND<>(OperandStack::popInt, x -> x >= 0, "ifge", pc);
  }

  public static IFCOND<Integer> IFGT(ProgramCounter pc, MethodInfo method) {
    return new IFCOND<>(OperandStack::popInt, x -> x > 0, "ifgt", pc);
  }

  public static IFCOND<Integer> IFNULL(ProgramCounter pc, MethodInfo method) {
    return new IFCOND<>(OperandStack::popAddress, x -> x == 0, "ifnull", pc);
  }

  public static IFCOND<Integer> IFNONNULL(ProgramCounter pc, MethodInfo method) {
    return new IFCOND<>(OperandStack::popAddress, x -> x != 0, "ifnonnull", pc);
  }

  @Override
  public void run(JThread thread) {
    var stack = thread.top().stack();
    var value = popFunc.apply(stack);
    if (pred.test(value)) {
      thread.pc().move(offset);
    }
  }

  @Override
  public String toString() {
    return String.format("%s %d", name, offset);
  }
}
