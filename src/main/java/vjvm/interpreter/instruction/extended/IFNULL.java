package vjvm.interpreter.instruction.extended;

import lombok.var;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

public class IFNULL extends Instruction {
  @Override
  public void fetchAndRun(JThread thread) {
    var value = thread.top().stack().popAddress();
    var offset = thread.pc().short_();
    if (value == 0)
      thread.pc().move(offset - 3);
  }
}
