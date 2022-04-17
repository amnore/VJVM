package vjvm.interpreter.instruction.math;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.var;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.ProgramCounter;
import vjvm.runtime.classdata.MethodInfo;

import java.util.function.BiFunction;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LIOPR extends Instruction {
  private final BiFunction<Long, Integer, Long> opr;
  private final String name;

  public static LIOPR LSHL(ProgramCounter pc, MethodInfo method) {
    return new LIOPR((x, y) -> x << y, "lshl");
  }

  public static LIOPR LSHR(ProgramCounter pc, MethodInfo method) {
    return new LIOPR((x, y) -> x >> y, "lshr");
  }

  public static LIOPR LUSHR(ProgramCounter pc, MethodInfo method) {
    return new LIOPR((x, y) -> x >>> y, "lushr");
  }

  @Override
  public void run(JThread thread) {
    var stack = thread.top().stack();
    var right = stack.popInt();
    var left = stack.popLong();
    stack.pushLong(opr.apply(left, right));
  }

  @Override
  public String toString() {
    return name;
  }
}
