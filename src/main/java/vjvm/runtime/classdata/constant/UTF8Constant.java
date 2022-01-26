package vjvm.runtime.classdata.constant;

import lombok.Getter;
import lombok.SneakyThrows;

import java.io.DataInput;

@Getter
public class UTF8Constant extends Constant {
    private final String value;

    @SneakyThrows
    UTF8Constant(DataInput input) {
        value = input.readUTF();
    }
}
