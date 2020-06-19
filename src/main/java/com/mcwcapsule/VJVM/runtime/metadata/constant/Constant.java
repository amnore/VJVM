package com.mcwcapsule.VJVM.runtime.metadata.constant;

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

import org.apache.commons.lang3.tuple.Pair;

import lombok.val;

public class Constant {
    public static Pair<Constant, Integer> construntFromData(DataInput input) {
        try {
            Constant result = null;
            int count = 0;
            val tag = input.readByte();
            switch (tag) {
                case CONSTANT_Class:
                    result = new RawClassRef(input.readUnsignedShort());
                    count = 1;
                    break;
                case CONSTANT_Fieldref:
                case CONSTANT_Methodref:
                case CONSTANT_InterfaceMethodref:
                    val classIndex = input.readUnsignedShort();
                    val nameAndTypeIndex = input.readUnsignedShort();
                    result = tag == CONSTANT_Fieldref ? new RawFieldRef(classIndex, nameAndTypeIndex)
                            : tag == CONSTANT_Methodref ? new RawMethodRef(classIndex, nameAndTypeIndex)
                                    : new RawInterfaceMethodRef(classIndex, nameAndTypeIndex);
                    count = 1;
                    break;
                case CONSTANT_String:
                    result = new RawStringConstant(input.readUnsignedShort());
                    count = 1;
                    break;
                case CONSTANT_Integer:
                    result = new IntegerConstant(input.readInt());
                    count = 1;
                    break;
                case CONSTANT_Float:
                    result = new FloatConstant(input.readFloat());
                    count = 1;
                    break;
                case CONSTANT_Long:
                    result = new LongConstant(input.readLong());
                    count = 2;
                    break;
                case CONSTANT_Double:
                    result = new DoubleConstant(input.readDouble());
                    count = 2;
                    break;
                case CONSTANT_NameAndType:
                    val nameIndex = input.readUnsignedShort();
                    val descIndex = input.readUnsignedShort();
                    result = new RawNameAndTypeConstant(nameIndex, descIndex);
                    count = 1;
                    break;
                case CONSTANT_Utf8:
                    result = new UTF8Constant(input.readUTF());
                    count = 1;
                    break;
                default:
                    throw new ClassFormatError();
            }
            return Pair.of(result, count);
        } catch (IOException e) {
            throw new ClassFormatError();
        }
    }
}
