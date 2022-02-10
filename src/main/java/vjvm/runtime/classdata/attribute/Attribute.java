package vjvm.runtime.classdata.attribute;

import lombok.SneakyThrows;
import vjvm.runtime.classdata.ConstantPool;
import vjvm.runtime.classdata.constant.UTF8Constant;

import java.io.DataInput;

import static vjvm.classfiledefs.AttributeTags.*;

public abstract class Attribute {

  @SneakyThrows
  public static Attribute constructFromData(DataInput input, ConstantPool constantPool) {
    int nameIndex = input.readUnsignedShort();
    String name = ((UTF8Constant) constantPool.constant(nameIndex)).value();
    long attrLength = Integer.toUnsignedLong(input.readInt());

    switch (name) {
      case ATTR_ConstantValue:
        assert attrLength == 2;
        return new ConstantValue(input, constantPool);
      case ATTR_Code:
        return new Code(input, constantPool);
      case ATTR_NestHost:
        return new NestHost(input, constantPool);
      case ATTR_NestMembers:
        return new NestMember(input, constantPool);
      default:
        return new UnknownAttribute(input, attrLength);
    }
  }
}
