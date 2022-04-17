package vjvm.interpreter.instruction.references;

import lombok.var;
import vjvm.classfiledefs.Descriptors;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.ProgramCounter;
import vjvm.runtime.classdata.FieldInfo;
import vjvm.runtime.classdata.MethodInfo;
import vjvm.runtime.classdata.constant.FieldRef;

public class PUTFIELD extends Instruction {
  private final FieldInfo field;

  public PUTFIELD(ProgramCounter pc, MethodInfo method) {
    var cp = method.jClass().constantPool();
    var fieldRef = (FieldRef) cp.constant(pc.ushort());
    field = fieldRef.value();
  }

  @Override
  public void run(JThread thread) {
    var frame = thread.top();
    var stack = frame.stack();
    var heap = thread.context().heap();

    if (field.size() == 1) {
      var value = stack.popInt();
      var obj = heap.get(stack.popAddress());

      if (field.descriptor().charAt(0) == Descriptors.DESC_boolean)
        value &= 1;
      obj.data().int_(field.offset(), value);
    } else {
      var value = stack.popLong();
      var obj = heap.get(stack.popAddress());
      obj.data().long_(field.offset(), value);
    }
  }

  @Override
  public String toString() {
    return String.format("putfield %s", field.descriptor());
  }
}
