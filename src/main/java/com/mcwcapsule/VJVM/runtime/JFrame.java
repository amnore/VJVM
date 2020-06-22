package com.mcwcapsule.VJVM.runtime;

import com.mcwcapsule.VJVM.runtime.metadata.RuntimeConstantPool;
import lombok.Getter;

@Getter
public class JFrame {
    private final Slots localVars;
    private final OperandStack opStack;
    private final RuntimeConstantPool dynLink;
    private final ProgramCounter PC;

    public JFrame(int maxLocals, int maxStack, RuntimeConstantPool dynLink, byte[] code) {
        localVars = new Slots(maxLocals);
        opStack = new OperandStack(maxStack);
        this.dynLink = dynLink;
        PC = new ProgramCounter(code);
    }
}
