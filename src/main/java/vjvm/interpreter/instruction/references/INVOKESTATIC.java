package vjvm.interpreter.instruction.references;

import lombok.var;
import vjvm.interpreter.instruction.Instruction;
import vjvm.runtime.JClass;
import vjvm.runtime.JThread;
import vjvm.runtime.classdata.constant.MethodRef;

public class INVOKESTATIC extends Instruction {

  @Override
  public void fetchAndRun(JThread thread) {
    var frame = thread.top();
    var methodRef = (MethodRef) frame.link().constant(thread.pc().ushort());
    var jClass = methodRef.jClass();
    jClass.initialize(thread);
    assert jClass.initState() == JClass.InitState.INITIALIZED;

    var method = methodRef.value();
    var args = frame.stack().popSlots(method.argc());
    thread.context().interpreter().invoke(method, thread, args);
  }

}
