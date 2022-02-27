package vjvm.interpreter.instruction.constants;

import lombok.var;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JClass;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.constant.ClassRef;

public class LDC extends Instruction {

  @Override
  public void fetchAndRun(JThread thread) {
    var frame = thread.top();
    var stack = frame.stack();
    var index = thread.pc().ubyte();
    var constant = frame.link().constant(index);
    if (constant instanceof ClassRef) {
      stack.pushAddress(((ClassRef) constant).value().classObject().address());
    } else {
      var value = frame.link().constant(index).value();

      if (value instanceof Integer)
        stack.pushInt((Integer) value);
      else if (value instanceof Float)
        stack.pushFloat((Float) value);
      else if (value instanceof JClass)
        stack.pushAddress(((JClass) value).classObject().address());
      else
        throw new Error(String.format("Unsupported: %s", value));
    }
  }

}
