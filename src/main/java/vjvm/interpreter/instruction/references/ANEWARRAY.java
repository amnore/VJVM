package vjvm.interpreter.instruction.references;

import lombok.var;
import vjvm.classfiledefs.Descriptors;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JClass;
import vjvm.runtime.JThread;
import vjvm.runtime.ProgramCounter;
import vjvm.runtime.classdata.MethodInfo;
import vjvm.runtime.classdata.constant.ClassRef;
import vjvm.runtime.object.ArrayObject;

public class ANEWARRAY extends Instruction {
  private final JClass jClass;
  private final JClass arrayClass;

  public ANEWARRAY(ProgramCounter pc, MethodInfo method) {
    var index = pc.ushort();
    var cp = method.jClass().constantPool();
    jClass = ((ClassRef) cp.constant(index)).value();
    arrayClass = jClass.classLoader().loadClass('[' + Descriptors.of(jClass.name()));
  }

  @Override
  public void run(JThread thread) {
    var frame = thread.top();
    var stack = frame.stack();
    var count = stack.popInt();

    arrayClass.initialize(thread);
    assert arrayClass.initState() == JClass.InitState.INITIALIZED;

    var arr = new ArrayObject(arrayClass, count);
    stack.pushAddress(arr.address());
  }

  @Override
  public String toString() {
    return String.format("anewarray %s", jClass.name());
  }
}
