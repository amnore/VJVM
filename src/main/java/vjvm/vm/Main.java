package vjvm.vm;

import picocli.CommandLine;

import java.util.concurrent.Callable;

import static picocli.CommandLine.*;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
@Command(name = "vjvm", mixinStandardHelpOptions = true, version = "vjvm 0.0.1", description = "A toy JVM written in java")
public class Main implements Callable<Integer> {
    @Option(names = { "-cp",
            "--classpath" }, paramLabel = "CLASSPATH", description = "the class path to search, multiple paths should be separated by ':'")
    private String userClassPath = ".";

    @Parameters(index = "0")
    private String entryClass = "";

    @Parameters(index = "1..*")
    private String[] args = {};

    @Override
    public Integer call() {
        VMContext vmContext = new VMContext(userClassPath);
        vmContext.run(entryClass);
        return 0;
    }

    public static void main(String[] args) {
        System.exit(new CommandLine(new Main()).execute(args));
    }
}
