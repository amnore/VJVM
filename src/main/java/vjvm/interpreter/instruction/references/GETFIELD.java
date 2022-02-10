package vjvm.interpreter.instruction.references;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.FieldInfo;
import vjvm.runtime.classdata.constant.FieldRef;

public class GETFIELD extends Instruction {

  @Override
  public void fetchAndRun(JThread thread) {
    var frame = thread.top();
    var stack = frame.stack();
    var obj = stack.popAddress();
    var ref = (FieldRef) frame.link().constant(thread.pc().ushort());

    if (obj == 0)
      throw new NullPointerException();
    FieldInfo field;
    field = ref.value();

    var slots = thread.context().heap().get(obj).data();
    if (field.size() == 2)
      stack.pushLong(slots.long_(field.offset()));
    else stack.pushInt(slots.int_(field.offset()));
  }

}
