package com.mcwcapsule.VJVM.runtime.metadata.attribute;

import java.io.DataInput;
import java.io.IOException;

import com.mcwcapsule.VJVM.runtime.metadata.RuntimeConstantPool;
import com.mcwcapsule.VJVM.runtime.metadata.constant.ClassRef;
import com.mcwcapsule.VJVM.runtime.metadata.constant.UTF8Constant;
import com.mcwcapsule.VJVM.runtime.metadata.constant.ValueConstant;

import lombok.var;

import static com.mcwcapsule.VJVM.runtime.metadata.AttributeTags.*;

public class Attribute {
    public static Attribute constructFromData(DataInput input, RuntimeConstantPool constantPool) {
        try {
            Attribute ret = null;
            int nameIndex = input.readUnsignedShort();
            String name = ((UTF8Constant) constantPool.getConstant(nameIndex)).getValue();
            long attrLength = Integer.toUnsignedLong(input.readInt());
            switch (name) {
                case ATTR_ConstantValue:
                    assert attrLength == 2;
                    int valueIndex = input.readUnsignedShort();
                    ret = new ConstantValue(((ValueConstant) constantPool.getConstant(valueIndex)).getValue());
                    break;
                case ATTR_Code:
                    int maxStack = input.readUnsignedShort();
                    int maxLocals = input.readUnsignedShort();
                    int codeLength = input.readInt();
                    byte[] code = new byte[codeLength];
                    input.readFully(code);
                    int exceptionTableLength = input.readUnsignedShort();
                    // skip exception table
                    assert input.skipBytes(exceptionTableLength * 8) == exceptionTableLength * 8;
                    int attributesCount = input.readUnsignedShort();
                    Attribute[] attributes = new Attribute[attributesCount];
                    for (int i = 0; i < attributesCount; ++i)
                        attributes[i] = constructFromData(input, constantPool);
                    ret = new Code(maxStack, maxLocals, code, attributes);
                default:
                    // return fake attribute
                    assert input.skipBytes((int) attrLength) == attrLength;
                    ret = new Attribute();
                    break;
            }
            return ret;
        } catch (IOException e) {
            throw new ClassFormatError();
        }
    }
}
