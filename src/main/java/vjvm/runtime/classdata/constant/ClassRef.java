package vjvm.runtime.classdata.constant;

import vjvm.runtime.JClass;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ClassRef extends ResolvableConstant {
    private final String name;
    private JClass jClass;

    @Override
    public void resolve(JClass thisClass) throws ClassNotFoundException {
        // if already resolved, to nothing
        if (jClass != null)
            return;
        // check whether the reference points to this class
        if (name.equals(thisClass.getThisClass().name))
            jClass = thisClass;
            // if not, load the Class using the defining class loader of this class
        else
            jClass = thisClass.getClassLoader().loadClass(name);
        // check accessibility
        // workaround for arrays
        if (thisClass.isArray())
            return;
        if (!jClass.isAccessibleTo(thisClass))
            throw new IllegalAccessError();
    }

    @Override
    public String toString() {
        return "ClassRef{" + "name='" + name + '\'' +
            '}';
    }
}
