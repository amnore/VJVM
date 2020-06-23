package com.mcwcapsule.VJVM.runtime;

import com.mcwcapsule.VJVM.classfiledefs.FieldDescriptors;
import com.mcwcapsule.VJVM.classloader.JClassLoader;
import com.mcwcapsule.VJVM.runtime.classdata.FieldInfo;
import com.mcwcapsule.VJVM.runtime.classdata.MethodInfo;
import com.mcwcapsule.VJVM.runtime.classdata.RuntimeConstantPool;
import com.mcwcapsule.VJVM.runtime.classdata.attribute.Attribute;
import com.mcwcapsule.VJVM.runtime.classdata.attribute.ConstantValue;
import com.mcwcapsule.VJVM.runtime.classdata.constant.ClassRef;
import com.mcwcapsule.VJVM.vm.VJVM;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.mcwcapsule.VJVM.classfiledefs.ClassAccessFlags.*;
import static com.mcwcapsule.VJVM.classfiledefs.FieldDescriptors.*;

public class JClass {
    // inititlized with constructor
    @Getter
    protected short minorVersion;
    @Getter
    protected short majorVersion;
    @Getter
    protected RuntimeConstantPool constantPool;
    @Getter
    protected short accessFlags;
    @Getter
    protected ClassRef thisClass;
    @Getter
    protected ClassRef superClass;
    protected ClassRef[] interfaces;
    protected FieldInfo[] fields;
    protected MethodInfo[] methods;
    protected Attribute[] attributes;
    @Getter
    protected JClassLoader classLoader;
    @Getter
    protected String packageName;

    // initialized with prepare()
    @Getter
    protected Slots staticFields;
    // size of instance object
    @Getter
    protected int instanceSize;
    protected int methodAreaIndex;

    @Getter
    @Setter
    protected int initState;
    // the thread calling the initialize method
    private JThread initThread;

    protected JClass() {
    }

    public void tryVerify() {
        // not verifying
        initState = InitState.VERIFIED;
    }

    /**
     * Prepares this class for use. See spec. 5.4.2
     * Invoking this method when the class has been prepared has no effect.
     */
    public void tryPrepare() {
        if (initState >= InitState.PREPARED)
            return;
        // first verify
        tryVerify();
        // prepare super classes and super interfaces
        if (superClass != null)
            superClass.getJClass().tryPrepare();
        for (val i : interfaces)
            i.getJClass().tryPrepare();
        initState = InitState.PREPARING;
        // create static fields
        int staticSize = 0;
        val staticFieldInfos = Arrays.stream(fields).filter(FieldInfo::isStatic).collect(Collectors.toList());
        for (val field : staticFieldInfos) {
            field.setOffset(staticSize);
            staticSize += FieldDescriptors.getSize(field.getDescriptor());
        }
        staticFields = new Slots(staticSize);
        // load ConstantValue
        for (val field : staticFieldInfos) {
            if (!field.isFinal())
                continue;
            for (int i = 0; i < field.getAttributeCount(); ++i)
                if (field.getAttribute(i) instanceof ConstantValue) {
                    int offset = field.getOffset();
                    Object value = ((ConstantValue) field.getAttribute(i)).getValue();
                    switch (field.getDescriptor().charAt(0)) {
                        case DESC_boolean:
                        case DESC_byte:
                        case DESC_char:
                        case DESC_short:
                        case DESC_int:
                            // The stored value is an Integer
                            staticFields.setInt(offset, (Integer) value);
                            break;
                        case DESC_float:
                            staticFields.setFloat(offset, (Float) value);
                            break;
                        case DESC_long:
                            staticFields.setLong(offset, (Long) value);
                            break;
                        case DESC_double:
                            staticFields.setDouble(offset, (Double) value);
                            break;
                    }
                    break;
                }
        }

        // init instance fields
        instanceSize = superClass == null ? 0 : superClass.getJClass().instanceSize;
        for (val field : fields) {
            if (field.isStatic())
                continue;
            field.setOffset(instanceSize);
            instanceSize += field.getSize();
        }
        initState = InitState.PREPARED;
    }

    /**
     * Initialize the class, see spec. 5.5.
     * Invoking this method when the class has been initialized has no effect.
     * The steps correspond to that specified in spec. 5.5.
     *
     * @param thread the thread at which to execute initialization method
     */
    public void tryInitialize(JThread thread) {
        // prepare first
        tryPrepare();

        // step1: there is no LC

        // step2: instead of blocking, just fail
        if (initState == InitState.INITIALIZING && initThread != thread)
            throw new Error();

        // step3
        if (initThread == thread)
            return;

        // step4
        if (initState == InitState.INITIALIZED) return;

        // step5: not checking

        // step6
        initState = InitState.INITIALIZING;
        initThread = thread;

        // step7
        if (superClass != null)
            superClass.getJClass().tryInitialize(thread);
        for (val i : interfaces)
            i.getJClass().tryInitialize(thread);

        // step8: not doing

        // step9
        // find <clinit>
        MethodInfo clinit = null;
        for (val i : methods)
            if (i.getName().equals("<clinit>") && i.getDescriptor().equals("()V")) {
                clinit = i;
                break;
            }
        if (clinit != null) {
            thread.pushFrame(new JFrame(clinit));
            VJVM.getInterpreter().run(thread);
        }

        // step10
        initState = InitState.INITIALIZED;

        // step11,12: not doing
    }

    /**
     * Finds a field recursively. This is used to resolve field references. See spec. 5.4.3.2
     *
     * @param name       name of the field to find
     * @param descriptor descriptor of the field to find
     * @return the found field, or null if not found
     */
    public FieldInfo findField(String name, String descriptor) {
        for (val field : fields)
            if (field.getName().equals(name) && field.getDescriptor().equals(descriptor))
                return field;
        // find in super interfaces
        for (val si : interfaces) {
            // according to spec. 5.3.5.3, the reference to super interfaces have already been resolved.
            val result = si.getJClass().findField(name, descriptor);
            if (result != null)
                return result;
        }
        // then find in super class
        return superClass == null ? null : superClass.getJClass().findField(name, descriptor);
    }

    /**
     * Similar to findField, but find in super classes first. See spec. 5.4.3.3
     */
    public MethodInfo findMethod(String name, String descriptor) {
        for (val method : methods)
            if (method.getName().equals(name) && method.getDescriptor().equals(descriptor))
                return method;
        if (superClass != null) {
            val result = superClass.getJClass().findMethod(name, descriptor);
            if (result != null)
                return result;
        }
        // Using the rules from JDK7 instead of JDK8
        for (val si : interfaces) {
            val result = si.getJClass().findMethod(name, descriptor);
            if (result != null)
                return result;
        }
        return null;
    }

    public ClassRef getSuperInterface(int index) {
        return interfaces[index];
    }

    public int getSuperInterfacesCount() {
        return interfaces.length;
    }

    public Pair<String, JClassLoader> getRuntimePackage() {
        return Pair.of(packageName, classLoader);
    }

    /**
     * Checks whether this class is a subclass of another class. Super interfaces are not taken into account.
     *
     * @param other the class to check against.
     * @return Whether this class is a subclass of other. Returns false if this == other.
     */
    public boolean isSubclassOf(JClass other) {
        return superClass.getJClass() == other || (superClass != null && superClass.getJClass().isSubclassOf(other));
    }

    public boolean isAccessibleTo(JClass other) {
        return isPublic() || getRuntimePackage().equals(other.getRuntimePackage());
    }

    public boolean isPublic() {
        return (accessFlags & ACC_PUBLIC) != 0;
    }

    public boolean isFinal() {
        return (accessFlags & ACC_FINAL) != 0;
    }

    public boolean isSuper() {
        return (accessFlags & ACC_SUPER) != 0;
    }

    public boolean isInterface() {
        return (accessFlags & ACC_INTERFACE) != 0;
    }

    public boolean isAbstract() {
        return (accessFlags & ACC_ABSTRACT) != 0;
    }

    public boolean isSynthetic() {
        return (accessFlags & ACC_SYNTHETIC) != 0;
    }

    public boolean isAnnotation() {
        return (accessFlags & ACC_ANNOTATION) != 0;
    }

    public boolean isEnum() {
        return (accessFlags & ACC_ENUM) != 0;
    }

    public boolean isModule() {
        return (accessFlags & ACC_MODULE) != 0;
    }

    public static class InitState {
        public static final int LOADED = 0;
        public static final int VERIFYING = 1;
        public static final int VERIFIED = 2;
        public static final int PREPARING = 3;
        public static final int PREPARED = 4;
        public static final int INITIALIZING = 5;
        public static final int INITIALIZED = 6;
    }
}
