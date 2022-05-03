package lab2;

public class Conv {
  public static void main(String[] args) {
    IOUtil.writeInt((int) -1L);
    IOUtil.writeInt((byte) -2);
    IOUtil.writeInt((char) -3);
    IOUtil.writeInt((short) -4);
    IOUtil.writeInt((int) 5f);
    IOUtil.writeInt((int) 6.0);

    IOUtil.writeLong(7);
    IOUtil.writeLong((long) -8f);
    IOUtil.writeLong((long) 9.0);

    IOUtil.writeFloat(-10);
    IOUtil.writeFloat(11L);
    IOUtil.writeFloat((float) -12.0);

    IOUtil.writeDouble(-13);
    IOUtil.writeDouble(14L);
    IOUtil.writeDouble(-15.0f);
  }
}
