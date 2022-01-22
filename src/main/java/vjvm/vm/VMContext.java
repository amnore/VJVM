package vjvm.vm;

import static vjvm.utils.ClassPathUtil.findJavaPath;

import java.util.ArrayList;

import lombok.Getter;
import vjvm.classloader.JClassLoader;
import vjvm.interpreter.JInterpreter;
import vjvm.runtime.JHeap;
import vjvm.runtime.JThread;
import vjvm.runtime.Slots;
import vjvm.utils.InvokeUtil;

public class VMContext {
    private static final String bootstrapClassPath = findJavaPath();
    private static final ArrayList<JThread> threads = new ArrayList<>();
    @Getter
    private static JClassLoader bootstrapLoader;
    @Getter
    private static JInterpreter interpreter;
    @Getter
    private static JHeap heap;
    @Getter
    private static JClassLoader userLoader;
    private final int heapSize = 1024;

    VMContext(String userClassPath) {
        interpreter = new JInterpreter();
        heap = new JHeap(heapSize);
        bootstrapLoader = new JClassLoader(null, VMContext.bootstrapClassPath);
        userLoader = new JClassLoader(VMContext.bootstrapLoader, userClassPath);
        var initThread = new JThread();
        threads.add(initThread);

        // hack: string
        var strClass = bootstrapLoader.loadClass("java/lang/String");
        strClass.tryInitialize(initThread);
        // hack: Class
        var classClass = bootstrapLoader.loadClass("java/lang/Class");
        classClass.tryInitialize(initThread);
    }

    void run(String entryClass) {
        var initThread = threads.get(0);
        var initClass = userLoader.loadClass(entryClass.replace('.', '/'));
        initClass.tryInitialize(initThread);

        var mainMethod = initClass.findMethod("main", "([Ljava/lang/String;)V");
        assert mainMethod.jClass() == initClass;
        InvokeUtil.invokeMethodWithArgs(mainMethod, initThread, new Slots(1));
        VMContext.interpreter.run(initThread);
    }
}
