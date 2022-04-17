package vjvm.interpreter.instruction.references;

import lombok.var;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JClass;
import vjvm.runtime.JThread;
import vjvm.runtime.ProgramCounter;
import vjvm.runtime.classdata.FieldInfo;
import vjvm.runtime.classdata.MethodInfo;
import vjvm.runtime.classdata.constant.FieldRef;

public class GETSTATIC extends Instruction {
  private final JClass jClass;
  private final FieldInfo field;

  public GETSTATIC(ProgramCounter pc, MethodInfo method) {
    var cp = method.jClass().constantPool();
    var ref = (FieldRef) cp.constant(pc.ushort());
    field = ref.value();
    jClass = field.jClass();
  }

  @Override
  public void run(JThread thread) {
    var frame = thread.top();
    var stack = frame.stack();

    jClass.initialize(thread);
    assert jClass.initState() == JClass.InitState.INITIALIZED;

    if (field.size() == 2)
      stack.pushLong(jClass.staticFields().long_(field.offset()));
    else
      stack.pushInt(jClass.staticFields().int_(field.offset()));
  }

  @Override
  public String toString() {
    return String.format("getstatic %s", field.descriptor());
  }
}
