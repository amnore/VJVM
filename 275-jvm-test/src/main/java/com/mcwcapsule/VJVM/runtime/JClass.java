package com.mcwcapsule.VJVM.runtime;

import com.mcwcapsule.VJVM.classfiledefs.FieldDescriptors;
import com.mcwcapsule.VJVM.classloader.JClassLoader;
import com.mcwcapsule.VJVM.runtime.classdata.FieldInfo;
import com.mcwcapsule.VJVM.runtime.classdata.MethodInfo;
import com.mcwcapsule.VJVM.runtime.classdata.RuntimeConstantPool;
import com.mcwcapsule.VJVM.runtime.classdata.attribute.Attribute;
import com.mcwcapsule.VJVM.runtime.classdata.attribute.ConstantValue;
import com.mcwcapsule.VJVM.runtime.classdata.constant.ClassRef;
import com.mcwcapsule.VJVM.utils.InvokeUtil;
import com.mcwcapsule.VJVM.vm.VJVM;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
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
    protected ArrayList<MethodInfo> vtable;
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

        // init vtable
        // copy super's vtable
        if (superClass != null)
            vtable = new ArrayList<>(superClass.getJClass().vtable);
        else vtable = new ArrayList<>();
        // insert or override for each method, see spec. 5.4.5
        // transitive overriding is not considered
        // interface methods are not considered, unless they are implemented in super class
        // for other instance methods, they are all added to vtable
        int superlen = vtable.size();
        outer:
        for (val mc : methods) {
            if (mc.isStatic()) continue;
            for (int j = 0; j < superlen; ++j) {
                val ma = vtable.get(j);
                if (!mc.getName().equals(ma.getName()) || !mc.getDescriptor().equals(ma.getDescriptor()))
                    continue;
                if (mc.isPrivate())
                    continue;
                if (ma.isPublic() || ma.isProtected()
                    || (!ma.isPrivate() && ma.getJClass().getPackageName().equals(packageName))) {
                    vtable.set(j, mc);
                    mc.setVtableIndex(j);
                    continue outer;
                }
            }
            // if there is not override found
            vtable.add(mc);
            mc.setVtableIndex(vtable.size() - 1);
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
            InvokeUtil.invokeMethodWithArgs(clinit, thread, null);
            VJVM.getInterpreter().run(thread);
        }

        // step10
        initState = InitState.INITIALIZED;

        // step11,12: not doing

        // debug
        System.err.println(this);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("JClass{");
        sb.append("thisClass=").append(thisClass);
        sb.append(", superClass=").append(superClass);
        sb.append(", interfaces=").append(Arrays.toString(interfaces));
        sb.append(", fields=").append(Arrays.toString(fields));
        sb.append(", methods=").append(Arrays.toString(methods));
        sb.append(", vtable=").append(vtable);
        sb.append('}');
        return sb.toString();
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

    public MethodInfo getVtableMethod(int index) {
        return vtable.get(index);
    }

    /**
     * Check whether this class can be cast to another class. See spec. 6.5.instanceof
     *
     * @param other the class to check against.
     * @return Whether this class can be cast to other.
     */
    public boolean canCastTo(JClass other) {
        if (this == other) return true;

        // check for super class and super interfaces
        if (superClass != null && superClass.getJClass().canCastTo(other))
            return true;
        for (val i : interfaces)
            if (i.getJClass().canCastTo(other))
                return true;

        // check for array cast
        if (!(this instanceof ArrayClass) || !(other instanceof ArrayClass))
            return false;
        return ((ArrayClass) this).getElementClass().getJClass().canCastTo(
            ((ArrayClass) other).getElementClass().getJClass());
    }

    /**
     * Check whether this class is a subclass of another class.
     * Interfaces are not taken into account.
     *
     * @param other the class to check against
     * @return Whether this class is a subclass of other. Returns false if this == other.
     */
    public boolean isSubClassOf(JClass other) {
        return superClass != null
            && (superClass.getJClass() == other || superClass.getJClass().isSubClassOf(other));
    }

    public boolean isAccessibleTo(JClass other) {
        // The accessibility of an array class is the same as its component type. See spec. 5.3.3.2
        if (this instanceof ArrayClass) {
            val elem = ((ArrayClass) this).getElementClass();
            // workaround: element is primitive type
            if (elem == null) return true;
            return elem.getJClass().isAccessibleTo(other);
        }
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
