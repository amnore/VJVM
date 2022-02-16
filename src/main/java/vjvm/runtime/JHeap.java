package vjvm.runtime;

import vjvm.runtime.object.JObject;
import vjvm.runtime.object.StringObject;
import vjvm.vm.VMContext;
import vjvm.vm.VMGlobalObject;

import java.util.HashMap;

/**
 * 模拟堆
 */
public class JHeap extends VMGlobalObject {
	private final HashMap<String, Integer> internMap;
	private final JObject[] objects;

	public JHeap(int heapSize, VMContext context) {
		super(context);
		internMap = new HashMap<>();
		objects = new JObject[heapSize];
	}

	public int intern(StringObject str) {
		return internMap.computeIfAbsent(str.value(), v -> str.address());
	}

	public int allocate(JObject object) {
		for (int i = 1; i < objects.length; i++) {
			if (objects[i] == null) {
				objects[i] = object;
				return i;
			}
		}

		return 0;
	}

	public JObject get(int address) {
		return objects[address];
	}
}
