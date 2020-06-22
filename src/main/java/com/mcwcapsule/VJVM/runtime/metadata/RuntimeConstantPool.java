package com.mcwcapsule.VJVM.runtime.metadata;

import java.io.DataInput;
import java.io.IOException;

import com.mcwcapsule.VJVM.runtime.JClass;
import com.mcwcapsule.VJVM.runtime.metadata.constant.Constant;
import com.mcwcapsule.VJVM.runtime.metadata.constant.ResolvableConstant;
import com.mcwcapsule.VJVM.runtime.metadata.constant.UnevaluatedConstant;

import lombok.Getter;
import lombok.val;

public class RuntimeConstantPool {
    // runtime constants are stored here
    private Constant[] constants;
    // number of constants
    private int count;
    @Getter
    private final JClass jClass;

    /**
     * Constructs a runtime constant pool from binary data
     * @param count number of constants
     * @param dataInput stream of data, contents of this constant pool will be read from stream
     */
    public RuntimeConstantPool(DataInput dataInput, JClass jClass) {
        this.jClass = jClass;
        try {
            this.count = dataInput.readUnsignedShort();
            constants = new Constant[count];
            for (int i = 1; i < count;) {
                val r = Constant.construntFromData(dataInput);
                constants[i] = r.getLeft();
                i += r.getRight();
            }
            // eval unevaluated constants
            for (int i = 1; i < count; ++i)
                if (constants[i] instanceof UnevaluatedConstant)
                    constants[i] = ((UnevaluatedConstant) constants[i]).evaluate(this);
        } catch (IOException e) {
            throw new ClassFormatError();
        }
    }

    public RuntimeConstantPool(Constant[] constants, JClass jClass) {
        this.count = constants.length;
        this.jClass = jClass;
        this.constants = constants;
    }

    /**
     * Gets a constant at index
     * @param index the index of the constant
     * @return the constant in the pool
     */
    public Constant getConstant(int index) {
        assert index > 0 && index < count;
        return constants[index];
    }

    public void setConstant(int index, Constant constant) {
        constants[index] = constant;
    }

    public int size() {
        return constants.length;
    }

}
