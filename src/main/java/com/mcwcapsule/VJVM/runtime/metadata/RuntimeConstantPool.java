package com.mcwcapsule.VJVM.runtime.metadata;

import static com.mcwcapsule.VJVM.runtime.metadata.ConstantTags.CONSTANT_Class;
import static com.mcwcapsule.VJVM.runtime.metadata.ConstantTags.CONSTANT_Double;
import static com.mcwcapsule.VJVM.runtime.metadata.ConstantTags.CONSTANT_Fieldref;
import static com.mcwcapsule.VJVM.runtime.metadata.ConstantTags.CONSTANT_Float;
import static com.mcwcapsule.VJVM.runtime.metadata.ConstantTags.CONSTANT_Integer;
import static com.mcwcapsule.VJVM.runtime.metadata.ConstantTags.CONSTANT_InterfaceMethodref;
import static com.mcwcapsule.VJVM.runtime.metadata.ConstantTags.CONSTANT_Long;
import static com.mcwcapsule.VJVM.runtime.metadata.ConstantTags.CONSTANT_Methodref;
import static com.mcwcapsule.VJVM.runtime.metadata.ConstantTags.CONSTANT_NameAndType;
import static com.mcwcapsule.VJVM.runtime.metadata.ConstantTags.CONSTANT_String;
import static com.mcwcapsule.VJVM.runtime.metadata.ConstantTags.CONSTANT_Utf8;

import java.io.DataInput;
import java.io.IOException;

import com.mcwcapsule.VJVM.runtime.metadata.constant.ClassRef;
import com.mcwcapsule.VJVM.runtime.metadata.constant.DoubleConstant;
import com.mcwcapsule.VJVM.runtime.metadata.constant.FieldRef;
import com.mcwcapsule.VJVM.runtime.metadata.constant.FloatConstant;
import com.mcwcapsule.VJVM.runtime.metadata.constant.IntegerConstant;
import com.mcwcapsule.VJVM.runtime.metadata.constant.InterfaceMethodRef;
import com.mcwcapsule.VJVM.runtime.metadata.constant.LongConstant;
import com.mcwcapsule.VJVM.runtime.metadata.constant.MethodRef;
import com.mcwcapsule.VJVM.runtime.metadata.constant.NameAndTypeConstant;
import com.mcwcapsule.VJVM.runtime.metadata.constant.StringConstant;
import com.mcwcapsule.VJVM.runtime.metadata.constant.UTF8Constant;

import lombok.val;

public class RuntimeConstantPool {
    // runtime constants are stored here
    private Object[] constants;
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
        constants = new Object[count + 1];
        this.count = count;
        constructFromData(dataInput);
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

    private void constructFromData(DataInput input) {
        for (int i = 1; i <= count; ++i) {
            try {
                val tag = input.readByte();
                switch (tag) {
                    case CONSTANT_Class:
                        constants[i] = new ClassRef(input.readUnsignedShort());
                        break;
                    case CONSTANT_Fieldref:
                    case CONSTANT_Methodref:
                    case CONSTANT_InterfaceMethodref:
                        val classIndex = input.readUnsignedShort();
                        val nameAndTypeIndex = input.readUnsignedShort();
                        constants[i] = tag == CONSTANT_Fieldref ? new FieldRef(classIndex, nameAndTypeIndex)
                                : tag == CONSTANT_Methodref ? new MethodRef(classIndex, nameAndTypeIndex)
                                        : new InterfaceMethodRef(classIndex, nameAndTypeIndex);
                        break;
                    case CONSTANT_String:
                        constants[i] = new StringConstant(input.readUnsignedShort());
                        break;
                    case CONSTANT_Integer:
                        constants[i] = new IntegerConstant(input.readInt());
                        break;
                    case CONSTANT_Float:
                        constants[i] = new FloatConstant(input.readFloat());
                        break;
                    case CONSTANT_Long:
                        constants[i] = new LongConstant(input.readLong());
                        break;
                    case CONSTANT_Double:
                        constants[i] = new DoubleConstant(input.readDouble());
                    case CONSTANT_NameAndType:
                        val nameIndex = input.readUnsignedShort();
                        val descIndex = input.readUnsignedShort();
                        constants[i] = new NameAndTypeConstant(nameIndex, descIndex);
                        break;
                    case CONSTANT_Utf8:
                        constants[i] = new UTF8Constant(input.readUTF());
                        break;
                    default:
                        throw new ClassFormatError();
                }
            } catch (IOException e) {
                throw new ClassFormatError();
            }
        }
    }
}
