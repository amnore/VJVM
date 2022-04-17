package vjvm.interpreter.instruction.references;

import lombok.var;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JClass;
import vjvm.runtime.JThread;
import vjvm.runtime.ProgramCounter;
import vjvm.runtime.classdata.MethodInfo;
import vjvm.runtime.classdata.constant.ClassRef;
import vjvm.runtime.object.JObject;

public class NEW extends Instruction {
  private final JClass jClass;

  public NEW(ProgramCounter pc, MethodInfo method) {
    var cp = method.jClass().constantPool();
    var classRef = (ClassRef) cp.constant(pc.ushort());
    jClass = classRef.value();
  }

  @Override
  public void run(JThread thread) {
    var frame = thread.top();

    jClass.initialize(thread);
    assert jClass.initState() == JClass.InitState.INITIALIZED;

    var obj = new JObject(jClass);
    frame.stack().pushAddress(obj.address());
  }

  @Override
  public String toString() {
    return String.format("new %s", jClass.name());
  }
}
