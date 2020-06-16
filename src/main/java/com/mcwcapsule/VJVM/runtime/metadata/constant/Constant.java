package com.mcwcapsule.VJVM.runtime.metadata.constant;

import java.io.DataInput;
import java.io.IOException;

import lombok.val;

import static com.mcwcapsule.VJVM.runtime.metadata.ConstantTags.*;

public class Constant {
    public static Constant construntFromData(DataInput input) {
        try {
            Constant result = null;
            val tag = input.readByte();
            switch (tag) {
                case CONSTANT_Class:
                    result = new ClassRef(input.readUnsignedShort());
                    break;
                case CONSTANT_Fieldref:
                case CONSTANT_Methodref:
                case CONSTANT_InterfaceMethodref:
                    val classIndex = input.readUnsignedShort();
                    val nameAndTypeIndex = input.readUnsignedShort();
                    result = tag == CONSTANT_Fieldref ? new FieldRef(classIndex, nameAndTypeIndex)
                            : tag == CONSTANT_Methodref ? new MethodRef(classIndex, nameAndTypeIndex)
                                    : new InterfaceMethodRef(classIndex, nameAndTypeIndex);
                    break;
                case CONSTANT_String:
                    result = new StringConstant(input.readUnsignedShort());
                    break;
                case CONSTANT_Integer:
                    result = new IntegerConstant(input.readInt());
                    break;
                case CONSTANT_Float:
                    result = new FloatConstant(input.readFloat());
                    break;
                case CONSTANT_Long:
                    result = new LongConstant(input.readLong());
                    break;
                case CONSTANT_Double:
                    result = new DoubleConstant(input.readDouble());
                case CONSTANT_NameAndType:
                    val nameIndex = input.readUnsignedShort();
                    val descIndex = input.readUnsignedShort();
                    result = new NameAndTypeConstant(nameIndex, descIndex);
                    break;
                case CONSTANT_Utf8:
                    result = new UTF8Constant(input.readUTF());
                    break;
                default:
                    throw new ClassFormatError();
            }
            return result;
        } catch (IOException e) {
            throw new ClassFormatError();
        }
    }
}
