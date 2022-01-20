package vjvm.runtime.classdata.constant;

public class IntegerConstant extends ValueConstant {
    public IntegerConstant(Integer value) {
        super(value);
    }

    @Override
    public Integer value() {
        return (Integer) value;
    }
}
