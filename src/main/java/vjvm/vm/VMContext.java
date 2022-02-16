package vjvm.vm;

import lombok.Getter;
import vjvm.classfiledefs.Descriptors;
import vjvm.classloader.JClassLoader;
import vjvm.classloader.searchpath.ClassSearchPath;
import vjvm.classloader.searchpath.ModuleSearchPath;
import vjvm.interpreter.JInterpreter;
import vjvm.runtime.JClass;
import vjvm.runtime.JHeap;
import vjvm.runtime.JThread;
import vjvm.runtime.Slots;

import java.lang.module.ModuleFinder;
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
		entry.initialize(initThread);
		assert entry.initState() == JClass.InitState.INITIALIZED;

		var mainMethod = entry.findMethod("main", "([Ljava/lang/String;)V", true);
		assert mainMethod.jClass() == entry;
		interpreter.invoke(mainMethod, initThread, new Slots(1));
	}
}
