package com.mcwcapsule.VJVM.runtime;

import com.mcwcapsule.VJVM.runtime.classdata.MethodInfo;
import com.mcwcapsule.VJVM.runtime.classdata.RuntimeConstantPool;
import lombok.Getter;
import lombok.val;

@Getter
public class JFrame {
    private final Slots localVars;
    private final OperandStack opStack;
    private final RuntimeConstantPool dynLink;
    private final MethodInfo methodInfo;
    private final JClass jClass;
    private final ProgramCounter PC;

    public JFrame(MethodInfo method) {
        val code = method.getCode();
        jClass = method.getJClass();
        localVars = new Slots(code.getMaxLocals());
        opStack = new OperandStack(code.getMaxStack());
        dynLink = jClass.getConstantPool();
        methodInfo = method;
        PC = new ProgramCounter(code.getCode());
    }
}
