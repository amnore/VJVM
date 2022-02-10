package vjvm.runtime.classdata.constant;

import lombok.SneakyThrows;

import java.io.DataInput;

public class IntegerConstant extends Constant {
  private final int value;

  @SneakyThrows
  IntegerConstant(DataInput input) {
    value = input.readInt();
  }

  @Override
  public Integer value() {
    return value;
  }

  @Override
  public String toString() {
    return String.format("Integer: %d", value);
  }
}
