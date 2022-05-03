package lab2;

public class Control {
  public static void main(String[] args) {
    if (-1 < 0) {
      IOUtil.writeInt(-1);
    } else {
      IOUtil.writeInt(0);
    }

    if (2147483647 + 1L < 0) {
      IOUtil.writeInt(-2147483648);
    } else {
      IOUtil.writeLong(2147483648L);
    }

    if (0.0f / 0.0f < 1.0f) {
      IOUtil.writeFloat(0.0f);
    } else {
      IOUtil.writeFloat(1.0f);
    }

    if (0.0f / 0.0f > 1.0f) {
      IOUtil.writeFloat(0.0f);
    } else {
      IOUtil.writeFloat(1.0f);
    }

    if (0.0 + 1.0 < 1.0 || 0.0 + 1.0 > 1.0) {
      IOUtil.writeFloat(1.0f);
    } else {
      IOUtil.writeFloat(0.0f);
    }

    int i = 1, j = 1;
    for (int k = 1; k < 10; k++) {
      i += j;
      j ^= i;
      i ^= j;
      j ^= i;
      IOUtil.writeInt(i);
    }

    int k = 1;
    do {
      k++;
      IOUtil.writeInt(k);
    } while (k < 5);

    while(--k != 0) {
      if (k % 2 == 0) {
        IOUtil.writeInt(k);
      }
    }
  }
}
