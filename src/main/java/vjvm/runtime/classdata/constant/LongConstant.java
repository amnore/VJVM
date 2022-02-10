package vjvm.runtime.classdata.constant;

import lombok.SneakyThrows;

import java.io.DataInput;

public class LongConstant extends Constant {
  private final long value;

  @SneakyThrows
  LongConstant(DataInput input) {
    value = input.readLong();
  }

  @Override
  public Long value() {
    return value;
  }

  @Override
  public String toString() {
    return String.format("Long: %d", value);
  }
}
