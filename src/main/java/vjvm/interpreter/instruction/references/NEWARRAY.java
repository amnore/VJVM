package vjvm.interpreter.instruction.references;

import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JClass;
import vjvm.runtime.JThread;
import vjvm.runtime.object.ArrayObject;

public class NEWARRAY extends Instruction {
  private static final String[] arrType = {
    null, null, null, null, "[Z", "[C", "[F", "[D", "[B", "[S", "[I", "[J"
  };

  @Override
  public void fetchAndRun(JThread thread) {
    var atype = thread.pc().ubyte();
    assert atype >= 4;

    JClass jClass = thread.context().bootstrapLoader().loadClass(arrType[atype]);
    jClass.initialize(thread);
    assert jClass.initState() == JClass.InitState.INITIALIZED;

    var stack = thread.top().stack();
    var arr = new ArrayObject(jClass, stack.popInt());
    stack.pushAddress(arr.address());
  }

}
