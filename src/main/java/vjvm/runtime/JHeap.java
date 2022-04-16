package vjvm.runtime;

import lombok.Getter;
import lombok.var;
import vjvm.runtime.object.JObject;
import vjvm.runtime.object.StringObject;
import vjvm.vm.VMContext;

import java.util.HashMap;

public class JHeap {
  private final HashMap<String, Integer> internMap;
  private final JObject[] objects;
  @Getter
  private final VMContext context;

  public JHeap(int heapSize, VMContext context) {
    this.context = context;
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
