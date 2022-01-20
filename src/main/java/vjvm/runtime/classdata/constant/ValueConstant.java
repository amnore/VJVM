package vjvm.runtime.classdata.constant;

import lombok.Getter;

public abstract class ValueConstant extends Constant {
    @Getter
    protected final Object value;

    public ValueConstant(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

}
