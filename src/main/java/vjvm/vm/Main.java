package vjvm.vm;

import picocli.CommandLine;
import vjvm.classloader.JClassLoader;
import vjvm.interpreter.JInterpreter;
import vjvm.runtime.JHeap;
import vjvm.runtime.JThread;
import vjvm.runtime.Slots;
import vjvm.utils.InvokeUtil;
import vjvm.utils.TestUtilException;
import lombok.val;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.concurrent.Callable;

import static picocli.CommandLine.*;

@Command(name = "vjvm", mixinStandardHelpOptions = true, version = "vjvm 0.0.1", description = "A toy JVM written in java")
public class Main implements Callable<Integer> {
    @Option(names = { "-cp",
            "--classpath" }, paramLabel = "CLASSPATH", description = "the class path to search, multiple paths should be separated by ':'")
    private String userClassPath = ".";

    @Parameters(index = "0")
    private String entryClass = "";

    @Parameters(index = "1..*")
    private String[] args = {};

    private VMContext vmContext;

    @Override
    public Integer call() {
        vmContext = new VMContext(userClassPath);
        vmContext.run(entryClass);
        return 0;
    }

    public static void main(String[] args) {
        System.exit(new CommandLine(new Main()).execute(args));
    }
}
