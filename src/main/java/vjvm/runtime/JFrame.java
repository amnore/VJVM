package vjvm.runtime;

import vjvm.runtime.classdata.ConstantPool;
import vjvm.runtime.classdata.MethodInfo;
import vjvm.vm.VMContext;
import lombok.Getter;

@Getter
public class JFrame {
    private final Slots localVars;
    private final OperandStack opStack;
    private final ConstantPool dynLink;
    private final MethodInfo methodInfo;
    private final JClass jClass;
    private final ProgramCounter pc;

    public JFrame(MethodInfo method, VMContext context) {
        var code = method.code();
        jClass = method.jClass();
        localVars = new Slots(code.maxLocals());
        opStack = new OperandStack(code.maxStack());
        dynLink = jClass.constantPool();
        methodInfo = method;
        pc = new ProgramCounter(code.code());
    }
}
