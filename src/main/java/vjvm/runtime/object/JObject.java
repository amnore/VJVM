package vjvm.runtime.object;

import lombok.var;
import lombok.Getter;
import vjvm.runtime.JClass;
import vjvm.runtime.Slots;
import vjvm.vm.VMContext;

@Getter
public class JObject {
  private final JClass type;
  private final Slots data;
  private final int address;

  public JObject(JClass jClass) {
    assert jClass.initState() == JClass.InitState.INITIALIZED
      || jClass.name().equals("java/lang/Class");

    this.type = jClass;
    this.data = new Slots(jClass.instanceSize());
    this.address = jClass.context().heap().allocate(this);
  }

  public VMContext context() {
    return type.context();
  }
}
