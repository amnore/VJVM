package com.mcwcapsule.VJVM.runtime.classdata;

import com.mcwcapsule.VJVM.runtime.JClass;
import com.mcwcapsule.VJVM.runtime.classdata.constant.Constant;
import com.mcwcapsule.VJVM.runtime.classdata.constant.UnevaluatedConstant;
import lombok.Getter;
import lombok.val;

import java.io.DataInput;
import java.io.IOException;

public class RuntimeConstantPool {
    @Getter
    private final JClass jClass;
    // runtime constants are stored here
    private final Constant[] constants;
    // number of constants
    private final int count;

    /**
     * Constructs a runtime constant pool from binary data
     *
     * @param dataInput stream of data, contents of this constant pool will be read from stream
     * @param jClass the class this pool belongs to
     */
    public RuntimeConstantPool(DataInput dataInput, JClass jClass) {
        this.jClass = jClass;
        try {
            this.count = dataInput.readUnsignedShort();
            constants = new Constant[count];
            for (int i = 1; i < count; ) {
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
     *
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
