package vjvm.runtime.classdata.attribute;

import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import vjvm.runtime.classdata.ConstantPool;

import java.io.DataInput;

public class ConstantValue extends Attribute {
  @NonNull
  @Getter
  private final Object value;

  @SneakyThrows
  public ConstantValue(DataInput input, ConstantPool constantPool) {
    int valueIndex = input.readUnsignedShort();
    this.value = constantPool.constant(valueIndex).value();
  }
}
