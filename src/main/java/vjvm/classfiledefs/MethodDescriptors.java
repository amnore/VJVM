package vjvm.classfiledefs;

import lombok.var;

import lombok.var;

import static vjvm.classfiledefs.Descriptors.DESC_array;
import static vjvm.classfiledefs.Descriptors.DESC_reference;

public class MethodDescriptors {
  public static int argc(String descriptor) {
    assert descriptor.startsWith("(");

    var argc = 0;
    for (int i = 1; i < descriptor.length(); ) {
      if (descriptor.charAt(i) == ')') break;
      argc += Descriptors.size(descriptor.charAt(i));

      // find the next argument
      while (descriptor.charAt(i) == DESC_array) ++i;
      if (descriptor.charAt(i) == DESC_reference)
        i = descriptor.indexOf(';', i) + 1;
      else ++i;
    }
    return argc;
  }

  public static char returnType(String descriptor) {
    assert descriptor.startsWith("(");

    var i = descriptor.indexOf(')') + 1;
    assert i < descriptor.length();
    return descriptor.charAt(i);
  }
}
