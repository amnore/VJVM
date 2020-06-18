package com.mcwcapsule.VJVM.runtime.metadata.constant;

import com.mcwcapsule.VJVM.runtime.JClass;

import lombok.Getter;

public class ClassRef extends Constant implements ResolvableConstant {
    private final int nameIndex;
    @Getter
    String name;

    @Getter
    JClass jClass;

    public ClassRef(int nameIndex) {
        this.nameIndex = nameIndex;
    }

    @Override
    public void resolve(JClass thisClass) throws ClassNotFoundException {
        // if already resolved, to nothing
        if (jClass != null)
            return;
        // resolve class name
        name = ((UTF8Constant) thisClass.getConstantPool().getConstant(nameIndex)).getValue();
        // check whether the reference points to this class
        if (name.equals(thisClass.getThisClass().name))
            jClass = thisClass;
        // if not, load the Class using the defining class loader of this class
        else
            jClass = thisClass.getClassLoader().loadClass(name);
        // check accessibility
        if (!jClass.isAccessibleTo(thisClass))
            throw new IllegalAccessError();
    }
}
