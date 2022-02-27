package vjvm.interpreter.instruction.references;

import lombok.var;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JClass;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.FieldInfo;
import vjvm.runtime.classdata.constant.FieldRef;

public class GETSTATIC extends Instruction {

  @Override
  public void fetchAndRun(JThread thread) {
    var frame = thread.top();
    var stack = frame.stack();
    FieldInfo field;
    JClass jClass;

    var ref = (FieldRef) frame.link().constant(thread.pc().ushort());
    field = ref.value();
    jClass = field.jClass();
    jClass.initialize(thread);
    assert jClass.initState() == JClass.InitState.INITIALIZED;

    if (field.size() == 2)
      stack.pushLong(jClass.staticFields().long_(field.offset()));
    else
      stack.pushInt(jClass.staticFields().int_(field.offset()));
  }

}
