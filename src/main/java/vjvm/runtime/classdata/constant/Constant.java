package vjvm.runtime.classdata.constant;

import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;

import java.io.DataInput;
import java.io.IOException;

import static vjvm.classfiledefs.ConstantTags.*;

public class Constant {
    @SneakyThrows
    public static Pair<Constant, Integer> construntFromData(DataInput input) {
        Constant result;
        int count;
        var tag = input.readByte();

        switch (tag) {
            case CONSTANT_Class -> {
                result = new RawClassRef(input.readUnsignedShort());
                count = 1;
            }
            case CONSTANT_Fieldref, CONSTANT_Methodref, CONSTANT_InterfaceMethodref -> {
                var classIndex = input.readUnsignedShort();
                var nameAndTypeIndex = input.readUnsignedShort();
                result = tag == CONSTANT_Fieldref ? new RawFieldRef(classIndex, nameAndTypeIndex)
                    : tag == CONSTANT_Methodref ? new RawMethodRef(classIndex, nameAndTypeIndex)
                    : new RawInterfaceMethodRef(classIndex, nameAndTypeIndex);
                count = 1;
            }
            case CONSTANT_String -> {
                result = new RawStringConstant(input.readUnsignedShort());
                count = 1;
            }
            case CONSTANT_Integer -> {
                result = new IntegerConstant(input.readInt());
                count = 1;
            }
            case CONSTANT_Float -> {
                result = new FloatConstant(input.readFloat());
                count = 1;
            }
            case CONSTANT_Long -> {
                result = new LongConstant(input.readLong());
                count = 2;
            }
            case CONSTANT_Double -> {
                result = new DoubleConstant(input.readDouble());
                count = 2;
            }
            case CONSTANT_NameAndType -> {
                var nameIndex = input.readUnsignedShort();
                var descIndex = input.readUnsignedShort();
                result = new RawNameAndTypeConstant(nameIndex, descIndex);
                count = 1;
            }
            case CONSTANT_Utf8 -> {
                result = new UTF8Constant(input.readUTF());
                count = 1;
            }

            // method handle, method type, dynamic, invoke dynamic are used by CharSequence
            case CONSTANT_MethodHandle -> {
                result = new Constant();
                input.skipBytes(3);
                count = 1;
            }
            case CONSTANT_MethodType -> {
                result = new Constant();
                input.skipBytes(2);
                count = 1;
            }
            case CONSTANT_Dynamic, CONSTANT_InvokeDynamic -> {
                result = new Constant();
                input.skipBytes(4);
                count = 1;
            }
            default -> throw new ClassFormatError();
        }
        return Pair.of(result, count);
    }
}
