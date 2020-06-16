package com.mcwcapsule.VJVM.runtime.metadata;

import java.io.DataInput;

import com.mcwcapsule.VJVM.runtime.metadata.constant.Constant;

public class RuntimeConstantPool {
    // runtime constants are stored here
    private Constant[] constants;
    // number of constants
    private int count;
    // whether the constants are resolved
    private boolean resolved = false;

    /**
     * Constructs a runtime constant pool from binary data
     * @param count number of constants
     * @param dataInput stream of data, contents of this constant pool will be read from stream
     */
    public RuntimeConstantPool(int count, DataInput dataInput) {
        constants = new Constant[count + 1];
        this.count = count;
        for (int i = 1; i <= count; ++i)
            constants[i] = Constant.construntFromData(dataInput);
    }

    /**
     * Gets a constant at index
     * @param index the index of the constant
     * @return the constant in the pool
     */
    public Object getConstant(int index) {
        assert index > 0 && index <= count;
        return constants[index];
    }

    public void resolve() {
        if (resolved)
            return;
        // TODO:resolve constants
    }

}
