package vjvm.interpreter.instruction.comparisons;

import lombok.var;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.ProgramCounter;
import vjvm.runtime.classdata.MethodInfo;
import vjvm.utils.BiIntPredicate;

public class IF_XCMPCOND extends Instruction {
  private final BiIntPredicate pred;
  private final String name;
  private final int offset;

  private IF_XCMPCOND(BiIntPredicate pred, String name, ProgramCounter pc) {
    this.pred = pred;
    this.name = name;
    this.offset = pc.short_() - 3;
  }

  public static IF_XCMPCOND IF_ACMPEQ(ProgramCounter pc, MethodInfo method) {
    return new IF_XCMPCOND((x, y) -> x == y, "if_acmpeq", pc);
  }

  public static IF_XCMPCOND IF_ACMPNE(ProgramCounter pc, MethodInfo method) {
    return new IF_XCMPCOND((x, y) -> x != y, "if_acmpne", pc);
  }

  public static IF_XCMPCOND IF_ICMPEQ(ProgramCounter pc, MethodInfo method) {
    return new IF_XCMPCOND((x, y) -> x == y, "if_icmpeq", pc);
  }

  public static IF_XCMPCOND IF_ICMPNE(ProgramCounter pc, MethodInfo method) {
    return new IF_XCMPCOND((x, y) -> x != y, "if_icmpne", pc);
  }

  public static IF_XCMPCOND IF_ICMPLT(ProgramCounter pc, MethodInfo method) {
    return new IF_XCMPCOND((x, y) -> x < y, "if_icmplt", pc);
  }

  public static IF_XCMPCOND IF_ICMPLE(ProgramCounter pc, MethodInfo method) {
    return new IF_XCMPCOND((x, y) -> x <= y, "if_icmple", pc);
  }

  public static IF_XCMPCOND IF_ICMPGE(ProgramCounter pc, MethodInfo method) {
    return new IF_XCMPCOND((x, y) -> x >= y, "if_icmpge", pc);
  }

  public static IF_XCMPCOND IF_ICMPGT(ProgramCounter pc, MethodInfo method) {
    return new IF_XCMPCOND((x, y) -> x > y, "if_icmpgt", pc);
  }

  @Override
  public void run(JThread thread) {
    var stack = thread.top().stack();
    var right = stack.popInt();
    var left = stack.popInt();
    var pc = thread.pc();
    if (pred.test(left, right))
      pc.move(offset);
  }

  @Override
  public String toString() {
    return String.format("%s %d", name, offset);
  }
}
