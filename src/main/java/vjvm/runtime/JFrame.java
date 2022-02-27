package vjvm.runtime;

import lombok.var;
import lombok.Getter;
import vjvm.runtime.classdata.ConstantPool;
import vjvm.runtime.classdata.MethodInfo;

@Getter
public class JFrame {
  private final Slots vars;
  private final OperandStack stack;
  private final ConstantPool link;
  private final MethodInfo method;
  private final JClass jClass;
  private final ProgramCounter pc;

  public JFrame(MethodInfo method, Slots args) {
    jClass = method.jClass();
    link = jClass.constantPool();
    this.method = method;

    if (method.native_()) {
      stack = null;
      pc = null;
      vars = new Slots(args.size());
    } else {
      var code = method.code();
      stack = new OperandStack(code.maxStack());
      pc = new ProgramCounter(code.code());
      vars = new Slots(code.maxLocals());
    }

    args.copyTo(0, args.size(), vars, 0);
  }
}
