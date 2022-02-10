package vjvm.interpreter.instruction.references;

import vjvm.classfiledefs.Descriptors;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JClass;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.constant.FieldRef;

public class PUTSTATIC extends Instruction {

  @Override
  public void fetchAndRun(JThread thread) {
    var frame = thread.top();
    var fieldRef = (FieldRef) frame.link().constant(thread.pc().ushort());
    var jClass = fieldRef.value().jClass();

    if (jClass.initState() != JClass.InitState.INITIALIZED) {
      jClass.initialize(thread);
      assert jClass.initState() == JClass.InitState.INITIALIZED;
    }

    var slots = jClass.staticFields();
    var field = fieldRef.value();
    var stack = frame.stack();
    if (field.size() == 2)
      slots.long_(field.offset(), stack.popLong());
    else {
      var value = stack.popInt();
      if (field.descriptor().charAt(0) == Descriptors.DESC_boolean)
        value &= 1;
      slots.int_(field.offset(), value);
    }
  }

}
