package vjvm.vm;

import lombok.var;
import lombok.Getter;
import vjvm.classfiledefs.Descriptors;
import vjvm.classloader.JClassLoader;
import vjvm.classloader.searchpath.ClassSearchPath;
import vjvm.classloader.searchpath.ModuleSearchPath;
import vjvm.interpreter.JInterpreter;
import vjvm.interpreter.JMonitor;
import vjvm.runtime.JClass;
import vjvm.runtime.JHeap;
import vjvm.runtime.JThread;
import vjvm.runtime.Slots;

import java.util.ArrayList;

public class VMContext {
  private static final int heapSize = 1024;
  // Classes to load at startup
  private static final String[] initClasses;

  static {
    initClasses = new String[]{
      "Ljava/lang/String;",
      "Ljava/lang/Class;",
      "Z", "B", "C", "D", "F", "I", "J", "S", "V",
    };
  }

  private final ArrayList<JThread> threads = new ArrayList<>();
  @Getter
  private final JClassLoader bootstrapLoader;
  @Getter
  private final JInterpreter interpreter;
  @Getter
  private final JHeap heap;
  @Getter
  private final JClassLoader userLoader;
  @Getter
  private final JMonitor monitor;

  public VMContext(String userClassPath) {
    interpreter = new JInterpreter();
    heap = new JHeap(heapSize, this);
    monitor = new JMonitor(this);

    bootstrapLoader = new JClassLoader(
      null,
      getSystemSearchPaths(),
      this
    );

    userLoader = new JClassLoader(
      bootstrapLoader,
      ClassSearchPath.constructSearchPath(userClassPath),
      this
    );
  }

  void run(String entryClass) {
    var initThread = new JThread(this);
    threads.add(initThread);

    for (var desc : initClasses) {
      var c = bootstrapLoader.loadClass(desc);
      assert c != null;
      c.initialize(initThread);
      assert c.initState() == JClass.InitState.INITIALIZED;
    }

    var entry = userLoader.loadClass(Descriptors.of(entryClass));
    if (entry == null) {
      throw new Error(String.format("class %s not found", entry));
    }
    entry.initialize(initThread);
    assert entry.initState() == JClass.InitState.INITIALIZED;

    var mainMethod = entry.findMethod("main", "([Ljava/lang/String;)V", true);
    assert mainMethod.jClass() == entry;
    interpreter.invoke(mainMethod, initThread, new Slots(1));
  }

  private static ClassSearchPath[] getSystemSearchPaths() {
    var bootClassPath = System.getProperty("sun.boot.class.path");

    if (bootClassPath != null) {
      return ClassSearchPath.constructSearchPath(bootClassPath);
    }

    // For compatibility with JDK9+
    return new ClassSearchPath[] { new ModuleSearchPath() };
  }
}
