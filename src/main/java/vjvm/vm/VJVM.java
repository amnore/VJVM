package vjvm.vm;

import picocli.CommandLine;
import vjvm.classloader.JClassLoader;
import vjvm.interpreter.JInterpreter;
import vjvm.runtime.JHeap;
import vjvm.runtime.JThread;
import vjvm.runtime.Slots;
import vjvm.utils.InvokeUtil;
import vjvm.utils.TestUtilException;
import lombok.Getter;
import lombok.val;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import static picocli.CommandLine.*;
import static vjvm.utils.ClassPathUtil.findJavaPath;

@Command(name = "vjvm", mixinStandardHelpOptions = true, version = "vjvm 0.0.1",
    description = "A toy JVM written in java")
public class VJVM implements Callable<Integer> {
    @Option(names = { "-cp", "--classpath" }, paramLabel = "CLASSPATH",
        description = "the class path to search, multiple paths should be separated by ':'")
    String userClassPath = ".";

    @Parameters(index = "0")
    String entryClass = "";

    @Parameters(index = "1..*")
    String[] args = {};

    final int heapSize = 1024;

    final static String bootstrapClassPath = findJavaPath();

    private static final ArrayList<JThread> threads = new ArrayList<>();
    @Getter
    static JClassLoader bootstrapLoader;
    @Getter
    static JClassLoader userLoader;
    @Getter
    private static JHeap heap;
    @Getter
    private static JInterpreter interpreter;

    public static void addThread(JThread thread) {
        threads.add(thread);
    }

    public static void removeThread(JThread thread) {
        threads.remove(thread);
    }

    @Override
    public Integer call() {
        interpreter = new JInterpreter();
        heap = new JHeap(heapSize);
        bootstrapLoader = new JClassLoader(null, bootstrapClassPath);
        userLoader = new JClassLoader(bootstrapLoader, userClassPath);
        val initThread = new JThread();
        addThread(initThread);

        // set err stream
        val myErr = new ByteArrayOutputStream();
        System.setErr(new PrintStream(myErr));
        val oldOut = System.out;
        val myOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(myOut));
        try {

            // hack: string
            val strClass = bootstrapLoader.loadClass("java/lang/String");
            strClass.tryInitialize(initThread);
            // hack: Class
            val classClass = bootstrapLoader.loadClass("java/lang/Class");
            classClass.tryInitialize(initThread);

            val initClass = userLoader.loadClass(entryClass.replace('.', '/'));
            initClass.tryInitialize(initThread);

            val mainMethod = initClass.findMethod("main", "([Ljava/lang/String;)V");
            assert mainMethod.getJClass() == initClass;
            // FIXME: call main with arguments
            InvokeUtil.invokeMethodWithArgs(mainMethod, initThread, new Slots(1));
            interpreter.run(initThread);
        } catch (TestUtilException e) {
            throw e;
        } catch (Exception e) {
            // print interpreter trace
            throw new Error(e);
        } finally {
            System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err)));
            System.err.println(myErr.toString());
            System.err.println("Output: ");
            System.err.println(myOut.toString());
            oldOut.print(myOut.toString());
        }

        return 0;
    }

    public static void main(String[] args) {
        System.exit(new CommandLine(new VJVM()).execute(args));
    }
}
