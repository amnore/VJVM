package vjvm.interpreter.instruction.references;

import lombok.var;
import vjvm.classfiledefs.Descriptors;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JClass;
import vjvm.runtime.JThread;
import vjvm.runtime.ProgramCounter;
import vjvm.runtime.classdata.FieldInfo;
import vjvm.runtime.classdata.MethodInfo;
import vjvm.runtime.classdata.constant.FieldRef;

public class PUTSTATIC extends Instruction {
  private final FieldInfo field;
  private final JClass jClass;

  public PUTSTATIC(ProgramCounter pc, MethodInfo method) {
    var cp = method.jClass().constantPool();
    var fieldRef = (FieldRef) cp.constant(pc.ushort());
    field = fieldRef.value();
    jClass = field.jClass();
  }

  @Override
  public void run(JThread thread) {
    var frame = thread.top();

    if (jClass.initState() != JClass.InitState.INITIALIZED) {
      jClass.initialize(thread);
      assert jClass.initState() == JClass.InitState.INITIALIZED;
    }

    var slots = jClass.staticFields();
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

  @Override
  public String toString() {
    return String.format("putstatic %s", field.descriptor());
  }
}
