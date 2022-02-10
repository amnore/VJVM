package vjvm.interpreter.instruction.constants;

import lombok.RequiredArgsConstructor;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

@RequiredArgsConstructor
public class ICONST_X extends Instruction {
  private final int value;

  @Override
  public void fetchAndRun(JThread thread) {
    thread.top().stack().pushInt(value);
  }

}
