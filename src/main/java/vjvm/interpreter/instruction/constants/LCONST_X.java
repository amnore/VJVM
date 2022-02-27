package vjvm.interpreter.instruction.constants;

import lombok.var;
import lombok.RequiredArgsConstructor;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

@RequiredArgsConstructor
public class LCONST_X extends Instruction {
  private final long value;

  @Override
  public void fetchAndRun(JThread thread) {
    thread.top().stack().pushLong(value);
  }

}
