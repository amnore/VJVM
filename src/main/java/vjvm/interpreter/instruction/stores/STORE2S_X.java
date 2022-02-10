package vjvm.interpreter.instruction.stores;

import lombok.RequiredArgsConstructor;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

@RequiredArgsConstructor
public class STORE2S_X extends Instruction {
  private final int index;

  @Override
  public void fetchAndRun(JThread thread) {
    var frame = thread.top();
    frame.vars().long_(index, frame.stack().popLong());
  }

}
