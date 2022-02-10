package vjvm.runtime.classdata.attribute;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import vjvm.runtime.JClass;
import vjvm.runtime.classdata.ConstantPool;
import vjvm.runtime.classdata.constant.ClassRef;

import java.io.DataInput;

@Getter
public class Code extends Attribute {
  private final int maxStack;
  private final int maxLocals;
  private final byte[] code;
  private final ExceptionHandler[] exceptionTable;
  private final Attribute[] attributes;

  @SneakyThrows
  Code(DataInput input, ConstantPool constantPool) {
    maxStack = input.readUnsignedShort();
    maxLocals = input.readUnsignedShort();
    int codeLength = input.readInt();
    code = new byte[codeLength];
    input.readFully(code);
    int exceptionTableLength = input.readUnsignedShort();
    exceptionTable = new Code.ExceptionHandler[exceptionTableLength];
    for (int i = 0; i < exceptionTableLength; ++i) {
      var startPC = input.readUnsignedShort();
      var endPC = input.readUnsignedShort();
      var handlerPC = input.readUnsignedShort();
      var catchType = input.readUnsignedShort();
      var catchClassRef =
        (ClassRef) (catchType == 0 ? null : constantPool.constant(catchType));
      exceptionTable[i] = new Code.ExceptionHandler(
        startPC, endPC, handlerPC, catchClassRef);
    }
    int attributesCount = input.readUnsignedShort();
    attributes = new Attribute[attributesCount];
    for (int i = 0; i < attributesCount; ++i)
      attributes[i] = constructFromData(input, constantPool);
  }

  @AllArgsConstructor
  @Getter
  public static class ExceptionHandler {
    private final int startPC;
    private final int endPC;
    private final int handlerPC;

    // If catchType is null, this can catch all exceptions.
    private final ClassRef catchType;

    public JClass catchType() {
      return catchType.value();
    }
  }
}

