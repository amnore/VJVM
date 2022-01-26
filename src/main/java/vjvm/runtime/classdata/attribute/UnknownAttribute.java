package vjvm.runtime.classdata.attribute;

import lombok.Getter;
import lombok.SneakyThrows;

import java.io.DataInput;

@Getter
public class UnknownAttribute extends Attribute {
    private final byte[] data;

    @SneakyThrows
    UnknownAttribute(DataInput input, long length) {
        data = new byte[(int) length];
        input.readFully(data);
    }
}
