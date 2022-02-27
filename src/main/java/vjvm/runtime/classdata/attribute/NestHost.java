package vjvm.runtime.classdata.attribute;

import lombok.var;
import lombok.Getter;
import lombok.SneakyThrows;
import vjvm.runtime.classdata.ConstantPool;
import vjvm.runtime.classdata.constant.ClassRef;

import java.io.DataInput;

@Getter
public class NestHost extends Attribute {
  private final ClassRef hostClass;

  @SneakyThrows
  NestHost(DataInput input, ConstantPool constantPool) {
    var idx = input.readUnsignedShort();
    hostClass = (ClassRef) constantPool.constant(idx);
  }
}
