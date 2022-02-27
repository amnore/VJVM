package vjvm.interpreter.instruction.comparisons;

import lombok.var;
import lombok.RequiredArgsConstructor;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.utils.BiIntPredicate;

@RequiredArgsConstructor
public class IF_ICMPCOND extends Instruction {
  private final BiIntPredicate pred;

  @Override
  public void fetchAndRun(JThread thread) {
    var stack = thread.top().stack();
    var right = stack.popInt();
    var left = stack.popInt();
    var pc = thread.pc();
    var offset = pc.short_();
    if (pred.test(left, right))
      pc.move(offset - 3);
  }
}
