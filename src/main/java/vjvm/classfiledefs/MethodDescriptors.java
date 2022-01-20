package vjvm.classfiledefs;


import static vjvm.classfiledefs.FieldDescriptors.DESC_array;
import static vjvm.classfiledefs.FieldDescriptors.DESC_reference;

public class MethodDescriptors {
    public static int argc(String descriptor) {
        assert descriptor.startsWith("(");
        var isParsingClass = false;
        var argc = 0;
        for (int i = 1; i < descriptor.length(); ) {
            if (descriptor.charAt(i) == ')') break;
            argc += FieldDescriptors.size(descriptor.charAt(i));

            // find the next argument
            while (descriptor.charAt(i) == DESC_array) ++i;
            if (descriptor.charAt(i) == DESC_reference)
                i = descriptor.indexOf(';', i) + 1;
            else ++i;
        }
        return argc;
    }
}
