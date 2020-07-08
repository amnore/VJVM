package com.mcwcapsule.VJVM.runtime.classdata.attribute;

import com.mcwcapsule.VJVM.runtime.classdata.ConstantPool;
import com.mcwcapsule.VJVM.runtime.classdata.constant.ClassRef;
import com.mcwcapsule.VJVM.runtime.classdata.constant.UTF8Constant;
import com.mcwcapsule.VJVM.runtime.classdata.constant.ValueConstant;
import lombok.val;

import java.io.DataInput;
import java.io.IOException;

import static com.mcwcapsule.VJVM.classfiledefs.AttributeTags.ATTR_Code;
import static com.mcwcapsule.VJVM.classfiledefs.AttributeTags.ATTR_ConstantValue;

public class Attribute {
    public static Attribute constructFromData(DataInput input, ConstantPool constantPool) {
        try {
            Attribute ret;
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
                    val exceptionTable = new Code.ExceptionHandler[exceptionTableLength];
                    for (int i = 0; i < exceptionTableLength; ++i) {
                        val startPC = input.readUnsignedShort();
                        val endPC = input.readUnsignedShort();
                        val handlerPC = input.readUnsignedShort();
                        val catchType = input.readUnsignedShort();
                        val catchClassRef =
                            (ClassRef) (catchType == 0 ? null : constantPool.getConstant(catchType));
                        if (catchClassRef != null) {
                            try {
                                catchClassRef.resolve(constantPool.getJClass());
                            } catch (ClassNotFoundException e) {
                                throw new Error(e);
                            }
                        }
                        exceptionTable[i] = new Code.ExceptionHandler(
                            startPC, endPC, handlerPC, catchClassRef == null ? null : catchClassRef.getJClass());
                    }
                    int attributesCount = input.readUnsignedShort();
                    Attribute[] attributes = new Attribute[attributesCount];
                    for (int i = 0; i < attributesCount; ++i)
                        attributes[i] = constructFromData(input, constantPool);
                    ret = new Code(maxStack, maxLocals, code, exceptionTable, attributes);
                    break;
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
