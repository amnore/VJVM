package com.mcwcapsule.VJVM.runtime;

import com.mcwcapsule.VJVM.runtime.metadata.RuntimeConstantPool;

import lombok.Getter;

@Getter
public class JFrame {
    private Slots localVars;
    private OperandStack opStack;
    private RuntimeConstantPool dynLink;
    private ProgramCounter PC;

    public JFrame(int maxLocals, int maxStack, RuntimeConstantPool dynLink, byte[] code) {
        localVars = new Slots(maxLocals);
        opStack = new OperandStack(maxStack);
        this.dynLink = dynLink;
        PC = new ProgramCounter(code);
    }
}
