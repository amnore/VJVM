package vjvm.runtime;

import vjvm.runtime.classdata.ConstantPool;
import vjvm.runtime.classdata.MethodInfo;
import lombok.Getter;

@Getter
public class JFrame {
    private final Slots vars;
    private final OperandStack stack;
    private final ConstantPool link;
    private final MethodInfo method;
    private final JClass jClass;
    private final ProgramCounter pc;

    public JFrame(MethodInfo method) {
        var code = method.code();
        jClass = method.jClass();
        vars = new Slots(code.maxLocals());
        stack = new OperandStack(code.maxStack());
        link = jClass.constantPool();
        this.method = method;
        pc = new ProgramCounter(code.code());
    }
}
