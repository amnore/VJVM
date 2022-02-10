package vjvm.vm;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class VMGlobalObject {
  private final VMContext context;
}
