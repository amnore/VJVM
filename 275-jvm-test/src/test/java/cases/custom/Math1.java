package cases.custom;

import static cases.TestUtil.equalFloat;
import static java.lang.Math.*;

public class Math1 {
    public static void main(String[] args) {
        equalFloat(0, (float) sin(0));
        equalFloat(1, (float) sin(PI / 2));
        equalFloat(abs(-1), 1);
        equalFloat((float) floor(1.2), 1);
        equalFloat((float) ceil(1.2), 2);
        equalFloat((float) exp(2), (float) (E * E));
        equalFloat((float) Math.pow(2, 2), 4);
    }
}
