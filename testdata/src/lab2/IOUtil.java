package lab2;

public class IOUtil {
  public static native int readInt();
  public static native long readLong();
  public static native char readChar();

  public static native void writeInt(int value);
  public static native void writeFloat(float value);
  public static native void writeLong(long value);
  public static native void writeDouble(double value);
  public static native void writeChar(char value);

  public static void main(String[] args) {
    IOUtil.writeInt(IOUtil.readInt());
    IOUtil.writeFloat(1.0f);
    IOUtil.writeLong(IOUtil.readLong());
    IOUtil.writeDouble(1.0);
    IOUtil.writeChar(IOUtil.readChar());
  }
}
