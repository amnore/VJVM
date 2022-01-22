package vjvm.classloader;

public class JClassNotFoundException extends RuntimeException {
    JClassNotFoundException(String className) {
        super(String.format("Could not find class %s", className));
    }
}
