package com.mcwcapsule.VJVM.runtime;

import com.mcwcapsule.VJVM.runtime.metadata.MethodInfo;
import com.mcwcapsule.VJVM.runtime.metadata.RuntimeConstantPool;
import lombok.Getter;
import lombok.val;

@Getter
public class JFrame {
    private final Slots localVars;
    private final OperandStack opStack;
    private final RuntimeConstantPool dynLink;
    private final MethodInfo methodInfo;
    private final ProgramCounter PC;

    public JFrame(RuntimeConstantPool dynLink, MethodInfo method) {
        val code = method.getCode();
        localVars = new Slots(code.getMaxLocals());
        opStack = new OperandStack(code.getMaxStack());
        this.dynLink = dynLink;
        methodInfo = method;
        PC = new ProgramCounter(code.getCode());
    }
}
