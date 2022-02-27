package vjvm.interpreter.instruction.stores;

import lombok.var;
import lombok.RequiredArgsConstructor;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

@RequiredArgsConstructor
public class STORE1S_X extends Instruction {
  private final int index;

  @Override
  public void fetchAndRun(JThread thread) {
    var frame = thread.top();
    frame.vars().int_(index, frame.stack().popInt());
  }

}
