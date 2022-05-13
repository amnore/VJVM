package vjvm.utils;

import lombok.var;
public class UnimplementedError extends Error {
  public UnimplementedError() {
  }

  public UnimplementedError(String message) {
    super(message);
  }
}
