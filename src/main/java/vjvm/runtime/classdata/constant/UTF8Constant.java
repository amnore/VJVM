package vjvm.runtime.classdata.constant;

import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.text.StringEscapeUtils;

import java.io.DataInput;

@Getter
public class UTF8Constant extends Constant {
    private final String value;

    @SneakyThrows
    UTF8Constant(DataInput input) {
        value = input.readUTF();
    }

    @Override
    public String toString() {
        return String.format("Utf8: \"%s\"", StringEscapeUtils.escapeJava(value));
    }
}
