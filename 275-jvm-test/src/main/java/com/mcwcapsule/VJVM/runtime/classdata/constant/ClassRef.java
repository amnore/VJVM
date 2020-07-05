package com.mcwcapsule.VJVM.runtime.classdata.constant;

import com.mcwcapsule.VJVM.runtime.ArrayClass;
import com.mcwcapsule.VJVM.runtime.JClass;
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
        if (thisClass instanceof ArrayClass)
            return;
        if (!jClass.isAccessibleTo(thisClass))
            throw new IllegalAccessError();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClassRef{");
        sb.append("name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
