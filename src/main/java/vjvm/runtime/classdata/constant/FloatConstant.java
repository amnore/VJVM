package vjvm.runtime.classdata.constant;

public class FloatConstant extends ValueConstant {
    public FloatConstant(Float value) {
        super(value);
    }

    @Override
    public Float getValue() {
        return (Float) value;
    }
}
