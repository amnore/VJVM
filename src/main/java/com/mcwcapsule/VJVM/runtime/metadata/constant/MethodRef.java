package com.mcwcapsule.VJVM.runtime.metadata.constant;

import com.mcwcapsule.VJVM.runtime.JClass;
import com.mcwcapsule.VJVM.runtime.metadata.MethodInfo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MethodRef extends Constant implements ResolvableConstant {
    private final ClassRef classRef;
    private final String name;
    private final String descriptor;
    private MethodInfo info;

    /**
     * Resolve the referenced method. See spec. 5.4.3.3
     */
    @Override
    public void resolve(JClass thisClass) throws ClassNotFoundException {
        classRef.resolve(thisClass);
        if (classRef.getJClass().isInterface())
            throw new IncompatibleClassChangeError();
        // ignore signature polymorphic methods
        info = classRef.getJClass().findMethod(name, descriptor);
        if (info == null)
            throw new NoSuchMethodError();
        if (!info.isAccessibleTo(thisClass, classRef.getJClass()))
            throw new IllegalAccessError();
    }
}
