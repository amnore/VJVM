package vjvm.vm;

import picocli.CommandLine;
import vjvm.classfiledefs.Descriptors;
import vjvm.runtime.JClass;
import vjvm.runtime.classdata.constant.*;

import java.util.concurrent.Callable;

import static picocli.CommandLine.*;

@Command(name = "vjvm", mixinStandardHelpOptions = true, version = "vjvm 0.0.1", description = "A toy JVM written in java", subcommands = {
        Run.class, Dump.class })
public class Main implements Callable<Integer> {
    @Option(names = { "-cp",
            "--classpath" }, paramLabel = "CLASSPATH", description = "the class path to search, multiple paths should be separated by ':'")
    String userClassPath = ".";

    @Override
    public Integer call() {
        CommandLine.usage(this, System.err);
        return -1;
    }

    public static void main(String[] args) {
        System.exit(new CommandLine(new Main()).execute(args));
    }
}

@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
@Command(name = "run", description = "Execute java program")
class Run implements Callable<Integer> {
    @ParentCommand
    private Main parent;

    @Parameters(index = "0", description = "Class to run, e.g. vjvm.vm.Main")
    private String entryClass = "";

    @Parameters(index = "1..*", description = "Arguments passed to java program")
    private String[] args = {};

    @Override
    public Integer call() {
        var ctx = new VMContext(parent.userClassPath);
        ctx.run(entryClass);
        return 0;
    }

}

@SuppressWarnings({ "FieldMayBeFinal", "FieldCanBeLocal" })
@Command(name = "dump", description = "Dump class file")
class Dump implements Callable<Integer> {
    @ParentCommand
    private Main parent;

    @Parameters(index = "0", description = "Class to dump, e.g. java.lang.String")
    private String className = "";

    @Override
    public Integer call() {
        ClassLoader.getPlatformClassLoader();
        var ctx = new VMContext(parent.userClassPath);
        var c = ctx.userLoader().loadClass(Descriptors.of(className));

        if (c == null) {
            System.err.printf("Cannot find class %s\n", className);
            return -1;
        }

        dump(c);
        return 0;
    }

    private void dump(JClass c) {
        var out = System.out;

        out.printf("""
                class name: %s
                minor version: %d
                major version: %d
                flags: 0x%x
                this class: %s
                super class: %s
                """, c.name(), c.minorVersion(), c.majorVersion(), c.accessFlags(), c.thisClass().name(),
                c.superClass().name());

        out.printf("\nconstant pool:\n");
        var p = c.constantPool();
        for (int i = 1; i < p.size();) {
            var v = p.constant(i);
            out.printf("#%d = %s\n", i, v);

            int size = 1;
            if (v instanceof LongConstant || v instanceof DoubleConstant) {
                size = 2;
            }
            i += size;
        }

        out.printf("\ninterfaces:\n");
        for (int i = 0; i < c.superInterfacesCount(); i++) {
            var s = c.superInterface(i);
            out.printf("%s\n", s.name());
        }

        out.printf("\nfields:\n");
        for (int i = 0; i < c.fieldsCount(); i++) {
            var f = c.field(i);
            out.printf("%s(0x%x): %s\n", f.name(), f.accessFlags(), f.descriptor());
        }

        out.printf("\nmethods:\n");
        for (int i = 0; i < c.methodsCount(); i++) {
            var m = c.method(i);
            out.printf("%s(0x%x): %s\n", m.name(), m.accessFlags(), m.descriptor());
        }
    }
}
