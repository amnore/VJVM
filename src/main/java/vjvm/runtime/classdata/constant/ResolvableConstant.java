package vjvm.runtime.classdata.constant;

import vjvm.runtime.JClass;
import lombok.Getter;

@Getter
public abstract class ResolvableConstant extends Constant {
    public abstract void resolve(JClass jClass);
}
