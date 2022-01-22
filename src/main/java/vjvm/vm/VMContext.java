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
import vjvm.runtime.classdata.constant.ClassRef;
import vjvm.utils.InvokeUtil;

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
        initPrimitiveClass();

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

        var initThread = new JThread(this);
        threads.add(initThread);

        // hack: string
        var strClass = bootstrapLoader.loadClass("java/lang/String");
        strClass.tryInitialize(initThread);
        // hack: Class
        var classClass = bootstrapLoader.loadClass("java/lang/Class");
        classClass.tryInitialize(initThread);
    }

    public JClass primitiveClass(String name) {
        var jClass = primitiveClasses.get(name);
        assert jClass != null;
        return jClass;
    }

    private void initPrimitiveClass() {
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
            var l = (String)c.getLeft();
            var r = (String)c.getRight();
            var class_ = new JClass(
                null,
                (short) 0,
                (short) 0,
                null,
                (short) (ACC_FINAL|ACC_PUBLIC),
                r,
                null,
                new String[0],
                new FieldInfo[0],
                new MethodInfo[0],
                null,
                this
            );

            primitiveClasses.put(r, class_);
            primitiveClasses.put(l, class_);
        }

        for (var c : primitiveClasses.values())
            c.initState(JClass.InitState.INITIALIZED);
    }

    void run(String entryClass) {
        var initThread = threads.get(0);
        var initClass = userLoader.loadClass(entryClass.replace('.', '/'));
        initClass.tryInitialize(initThread);

        var mainMethod = initClass.findMethod("main", "([Ljava/lang/String;)V");
        assert mainMethod.jClass() == initClass;
        InvokeUtil.invokeMethodWithArgs(mainMethod, initThread, new Slots(1));
        interpreter.run(initThread);
    }
}
