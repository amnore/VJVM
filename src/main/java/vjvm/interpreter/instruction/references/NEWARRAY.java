package vjvm.interpreter.instruction.references;

import lombok.var;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JClass;
import vjvm.runtime.JThread;
import vjvm.runtime.ProgramCounter;
import vjvm.runtime.classdata.MethodInfo;
import vjvm.runtime.object.ArrayObject;

public class NEWARRAY extends Instruction {
  private static final String[] arrType = {
    null, null, null, null, "[Z", "[C", "[F", "[D", "[B", "[S", "[I", "[J"
  };

  private final JClass jClass;

  public NEWARRAY(ProgramCounter pc, MethodInfo method) {
    var atype = pc.ubyte();
    assert atype >= 4;

    var cp = method.jClass().constantPool();
    jClass = cp.context().bootstrapLoader().loadClass(arrType[atype]);
  }

  @Override
  public void run(JThread thread) {
    jClass.initialize(thread);
    assert jClass.initState() == JClass.InitState.INITIALIZED;

    var stack = thread.top().stack();
    var arr = new ArrayObject(jClass, stack.popInt());
    stack.pushAddress(arr.address());
  }

  @Override
  public String toString() {
    return String.format("newarray %s", jClass.name());
  }
}
