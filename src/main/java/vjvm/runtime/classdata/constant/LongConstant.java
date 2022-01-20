package vjvm.runtime.classdata.constant;

public class LongConstant extends ValueConstant {
    public LongConstant(Long value) {
        super(value);
    }

    @Override
    public Long value() {
        return (Long) value;
    }
}
