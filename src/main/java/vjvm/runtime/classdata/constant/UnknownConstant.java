package vjvm.runtime.classdata.constant;

import lombok.SneakyThrows;

import java.io.DataInput;

public class UnknownConstant extends Constant {
  private final byte[] data;

  @SneakyThrows
  UnknownConstant(DataInput input, int length) {
    data = new byte[length];
    input.readFully(data);
  }

  @Override
  public byte[] value() {
    return data;
  }

  @Override
  public String toString() {
    return "Unknown:";
  }
}
