package vjvm.interpreter.instruction.references;

import lombok.var;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.ProgramCounter;
import vjvm.runtime.classdata.FieldInfo;
import vjvm.runtime.classdata.MethodInfo;
import vjvm.runtime.classdata.constant.FieldRef;

public class GETFIELD extends Instruction {
  private final FieldInfo field;

  public GETFIELD(ProgramCounter pc, MethodInfo method) {
    var cp = method.jClass().constantPool();
    var ref = (FieldRef) cp.constant(pc.ushort());
    field = ref.value();
  }

  @Override
  public void run(JThread thread) {
    var frame = thread.top();
    var stack = frame.stack();
    var obj = stack.popAddress();
    if (obj == 0)
      throw new NullPointerException();

    var slots = thread.context().heap().get(obj).data();
    if (field.size() == 2) {
      stack.pushLong(slots.long_(field.offset()));
    } else {
      stack.pushInt(slots.int_(field.offset()));
    }
  }

  @Override
  public String toString() {
    return String.format("getfield %s", field.descriptor());
  }
}
