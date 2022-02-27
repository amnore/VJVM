package vjvm.utils;

import lombok.var;
public class UnimplementedException extends RuntimeException {
  public UnimplementedException() {
  }

  public UnimplementedException(String message) {
    super(message);
  }
}
