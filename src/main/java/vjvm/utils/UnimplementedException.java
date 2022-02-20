package vjvm.utils;

public class UnimplementedException extends RuntimeException {
  public UnimplementedException() {
  }

  public UnimplementedException(String message) {
    super(message);
  }
}
