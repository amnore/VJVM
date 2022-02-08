package vjvm.vm;

import java.lang.module.ModuleFinder;
import java.util.ArrayList;
import java.util.HashMap;

import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import vjvm.classloader.JClassLoader;
import vjvm.classloader.searchpath.ClassSearchPath;
import vjvm.classloader.searchpath.ModuleSearchPath;
import vjvm.interpreter.JInterpreter;
import vjvm.runtime.JClass;
import vjvm.runtime.JHeap;
import vjvm.runtime.JThread;
import vjvm.runtime.Slots;
import vjvm.runtime.classdata.FieldInfo;
import vjvm.runtime.classdata.MethodInfo;

import static vjvm.classfiledefs.ClassAccessFlags.ACC_FINAL;
import static vjvm.classfiledefs.ClassAccessFlags.ACC_PUBLIC;

public class VMContext {
    private final HashMap<String, JClass> primitiveClasses = new HashMap<>();
    private final ArrayList<JThread> threads = new ArrayList<>();
    @Getter
    private final JClassLoader bootstrapLoader;
    @Getter
    private final JInterpreter interpreter;
    @Getter
    private final JHeap heap;
    @Getter
    private final JClassLoader userLoader;
    private static final int heapSize = 1024;

    VMContext(String userClassPath) {
        interpreter = new JInterpreter();
        heap = new JHeap(heapSize, this);

        bootstrapLoader = new JClassLoader(
            null,
            new ClassSearchPath[]{new ModuleSearchPath(ModuleFinder.ofSystem())},
            this
        );

        userLoader = new JClassLoader(
            bootstrapLoader,
            ClassSearchPath.constructSearchPath(userClassPath),
            this
        );
    }

    public JClass primitiveClass(String name) {
        var jClass = primitiveClasses.get(name);
        assert jClass != null;
        return jClass;
    }

    private void initPrimitiveClass(JThread thread) {
        var names = new Pair[] {
            Pair.of("boolean", "Z"),
            Pair.of("byte", "B"),
            Pair.of("char", "C"),
            Pair.of("double", "D"),
            Pair.of("float", "F"),
            Pair.of("int", "I"),
            Pair.of("long", "J"),
            Pair.of("short", "S"),
        };

        for (var c : names) {
            var name = (String)c.getLeft();
            var signature = (String)c.getRight();
            var cls = new JClass(
                bootstrapLoader,
                (short) (ACC_FINAL|ACC_PUBLIC),
                signature,
                null,
                new String[0],
                new FieldInfo[0],
                new MethodInfo[0]
            );

            primitiveClasses.put(signature, cls);
            primitiveClasses.put(name, cls);
        }

        for (var c : primitiveClasses.values())
            c.initialize(thread);
    }

    void run(String entryClass) {
        var initThread = new JThread(this);
        threads.add(initThread);
        initPrimitiveClass(initThread);

        var stringClass = bootstrapLoader.loadClass("java/lang/String");
        stringClass.initialize(initThread);
        assert stringClass.initState() == JClass.InitState.INITIALIZED;

        var initClass = userLoader.loadClass(entryClass);
        initClass.initialize(initThread);
        assert initClass.initState() == JClass.InitState.INITIALIZED;

        var mainMethod = initClass.findMethod("main", "([Ljava/lang/String;)V", true);
        assert mainMethod.jClass() == initClass;
        interpreter.invoke(mainMethod, initThread, new Slots(1));
    }
}
