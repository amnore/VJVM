package vjvm.interpreter.instruction.constants;

import lombok.var;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;

public class LDC_W extends Instruction {

  @Override
  public void fetchAndRun(JThread thread) {
    var frame = thread.top();
    var stack = frame.stack();
    var index = thread.pc().ushort();
    var value = frame.link().constant(index).value();

    // only int and float are supported
    if (value instanceof Integer)
      stack.pushInt((Integer) value);
    else if (value instanceof Float)
      stack.pushFloat((Float) value);
  }

}
