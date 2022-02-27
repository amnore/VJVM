package vjvm.interpreter.instruction.references;

import lombok.var;
import vjvm.classfiledefs.Descriptors;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.constant.FieldRef;

public class PUTFIELD extends Instruction {

  @Override
  public void fetchAndRun(JThread thread) {
    var frame = thread.top();
    var fieldRef = (FieldRef) frame.link().constant(thread.pc().ushort());

    var stack = frame.stack();
    var field = fieldRef.value();
    var heap = thread.context().heap();
    if (fieldRef.value().size() == 1) {
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

}
