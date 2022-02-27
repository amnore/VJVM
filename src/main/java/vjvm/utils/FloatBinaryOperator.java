package vjvm.utils;

import lombok.var;
@FunctionalInterface
public interface FloatBinaryOperator {
  float applyAsFloat(float t, float u);
}
