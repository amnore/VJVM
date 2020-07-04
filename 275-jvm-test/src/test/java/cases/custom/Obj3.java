package cases.custom;

import java.io.Serializable;

import static cases.TestUtil.reach;

public class Obj3 {
    public static void main(String[] args) {
        Obj3 a = new Obj4();
        if (a instanceof Cloneable)
            reach(1);
        else reach(2);
        Obj3[] b = new Obj4[0];
        if (b instanceof Object)
            reach(3);
        else reach(4);
        if (b instanceof Cloneable)
            reach(5);
        else reach(6);
        if (b instanceof Serializable)
            reach(7);
        else reach(8);
        if (b instanceof Obj3[])
            reach(9);
        else reach(10);
    }
}

class Obj4 extends Obj3 implements Cloneable {
}
