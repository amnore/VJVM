package vjvm.runtime.classdata.constant;

import vjvm.classfiledefs.MethodDescriptors;
import vjvm.runtime.JClass;
import vjvm.runtime.classdata.MethodInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MethodRef extends ResolvableConstant {
    private final ClassRef classRef;
    private final String name;
    private final String descriptor;
    private final boolean isInterfaceMethod;

    private MethodInfo info;
    private JClass jClass;

    /**
     * Resolve the referenced method. See spec. 5.4.3.3, 5.4.3.4.
     */
    @Override
    public void resolve(JClass thisClass) throws ClassNotFoundException {
        classRef.resolve(thisClass);
        jClass = classRef.getJClass();
        if (jClass.isInterface() ^ isInterfaceMethod)
            throw new IncompatibleClassChangeError();
        // ignore signature polymorphic methods
        info = jClass.findMethod(name, descriptor);
        if (info == null)
            throw new NoSuchMethodError();
        if (!info.isAccessibleTo(thisClass, jClass))
            throw new IllegalAccessError();
    }

    public int getArgc() {
        return MethodDescriptors.getArgc(descriptor);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MethodRef{");
        sb.append("name='").append(name).append('\'');
        sb.append(", descriptor='").append(descriptor).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
