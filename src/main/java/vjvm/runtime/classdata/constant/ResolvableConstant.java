package vjvm.runtime.classdata.constant;

import lombok.AccessLevel;
import vjvm.runtime.JClass;
import lombok.Getter;

@Getter(value = AccessLevel.PROTECTED)
public abstract class ResolvableConstant extends Constant {
    private final JClass thisClass;

    protected ResolvableConstant(JClass thisClass) {
        this.thisClass = thisClass;
    }

    public abstract void resolve();
}
