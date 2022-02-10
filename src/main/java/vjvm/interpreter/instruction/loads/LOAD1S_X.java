package vjvm.interpreter.instruction.loads;

import lombok.RequiredArgsConstructor;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

@RequiredArgsConstructor
public class LOAD1S_X extends Instruction {
  private final int index;

  @Override
  public void fetchAndRun(JThread thread) {
    var frame = thread.top();
    frame.stack().pushInt(frame.vars().int_(index));
  }

}
