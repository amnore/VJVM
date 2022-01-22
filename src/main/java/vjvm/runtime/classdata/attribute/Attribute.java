package vjvm.runtime.classdata.attribute;

import vjvm.runtime.classdata.ConstantPool;
import vjvm.runtime.classdata.constant.ClassRef;
import vjvm.runtime.classdata.constant.UTF8Constant;
import vjvm.runtime.classdata.constant.ValueConstant;
import lombok.val;

import java.io.DataInput;
import java.io.IOException;

import static vjvm.classfiledefs.AttributeTags.ATTR_Code;
import static vjvm.classfiledefs.AttributeTags.ATTR_ConstantValue;

public class Attribute {
    public static Attribute constructFromData(DataInput input, ConstantPool constantPool) {
        try {
            Attribute ret;
            int nameIndex = input.readUnsignedShort();
            String name = ((UTF8Constant) constantPool.constant(nameIndex)).value();
            long attrLength = Integer.toUnsignedLong(input.readInt());
            switch (name) {
                case ATTR_ConstantValue:
                    assert attrLength == 2;
                    int valueIndex = input.readUnsignedShort();
                    ret = new ConstantValue(((ValueConstant) constantPool.constant(valueIndex)).value());
                    break;
                case ATTR_Code:
                    int maxStack = input.readUnsignedShort();
                    int maxLocals = input.readUnsignedShort();
                    int codeLength = input.readInt();
                    byte[] code = new byte[codeLength];
                    input.readFully(code);
                    int exceptionTableLength = input.readUnsignedShort();
                    var exceptionTable = new Code.ExceptionHandler[exceptionTableLength];
                    for (int i = 0; i < exceptionTableLength; ++i) {
                        var startPC = input.readUnsignedShort();
                        var endPC = input.readUnsignedShort();
                        var handlerPC = input.readUnsignedShort();
                        var catchType = input.readUnsignedShort();
                        var catchClassRef =
                            (ClassRef) (catchType == 0 ? null : constantPool.constant(catchType));
                        if (catchClassRef != null) {
                            catchClassRef.resolve(constantPool.jClass());
                        }
                        exceptionTable[i] = new Code.ExceptionHandler(
                            startPC, endPC, handlerPC, catchClassRef == null ? null : catchClassRef.jClass());
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
