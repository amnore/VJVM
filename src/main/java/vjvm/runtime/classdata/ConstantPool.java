package vjvm.runtime.classdata;

import lombok.SneakyThrows;
import vjvm.runtime.JClass;
import vjvm.runtime.classdata.constant.Constant;
import vjvm.vm.VMContext;
import lombok.Getter;
import lombok.Setter;

import java.io.DataInput;

public class ConstantPool {
    @Getter
    @Setter
    private JClass jClass;
    // runtime constants are stored here
    private final Constant[] constants;
    // number of constants
    private final int count;

    /**
     * Constructs a runtime constant pool from binary data
     *
     * @param dataInput stream of data, contents of this constant pool will be read from stream
     * @param jClass    the class this pool belongs to
     */
    @SneakyThrows
    public ConstantPool(DataInput dataInput, JClass jClass) {
        this.jClass = jClass;
        this.count = dataInput.readUnsignedShort();
        constants = new Constant[count];
        for (int i = 1; i < count; ) {
            var r = Constant.construntFromData(dataInput, jClass);
            constants[i] = r.getLeft();
            i += r.getRight();
        }
    }

    public ConstantPool(Constant[] constants, JClass jClass) {
        this.count = constants.length;
        this.constants = constants;
        this.jClass = jClass;
    }

    /**
     * Gets a constant at index
     *
     * @param index the index of the constant
     * @return the constant in the pool
     */
    public Constant constant(int index) {
        assert index > 0 && index < count;
        return constants[index];
    }

    public void constant(int index, Constant constant) {
        constants[index] = constant;
    }

    public int size() {
        return constants.length;
    }

    public VMContext context() {
        return jClass.context();
    }
}
