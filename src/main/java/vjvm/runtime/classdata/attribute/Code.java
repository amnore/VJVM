package vjvm.runtime.classdata.attribute;

import vjvm.runtime.JClass;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Code extends Attribute {
    private final int maxStack;
    private final int maxLocals;
    private final byte[] code;
    private final ExceptionHandler[] exceptionTable;
    private final Attribute[] attributes;

    @AllArgsConstructor
    @Getter
    public static class ExceptionHandler {
        private final int startPC;
        private final int endPC;
        private final int handlerPC;

        // If catchType is null, this can catch all exceptions.
        private final JClass catchType;
    }
}

