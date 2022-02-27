package vjvm.interpreter.instruction.comparisons;

import lombok.var;
import lombok.RequiredArgsConstructor;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

import java.util.function.IntPredicate;

@RequiredArgsConstructor
public class IFCOND extends Instruction {
  private final IntPredicate pred;

  @Override
  public void fetchAndRun(JThread thread) {
    var stack = thread.top().stack();
    var value = stack.popInt();
    var pc = thread.pc();
    var offset = pc.short_();
    if (pred.test(value))
      pc.move(offset - 3);
  }

}
