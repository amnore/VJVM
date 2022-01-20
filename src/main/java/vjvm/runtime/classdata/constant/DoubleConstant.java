package vjvm.runtime.classdata.constant;

public class DoubleConstant extends ValueConstant {
    public DoubleConstant(Double value) {
        super(value);
    }

    @Override
    public Double value() {
        return (Double) value;
    }
}
