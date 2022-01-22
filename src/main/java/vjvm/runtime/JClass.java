package vjvm.runtime;

import vjvm.classfiledefs.FieldDescriptors;
import vjvm.classloader.JClassLoader;
import vjvm.runtime.classdata.ConstantPool;
import vjvm.runtime.classdata.FieldInfo;
import vjvm.runtime.classdata.MethodInfo;
import vjvm.runtime.classdata.attribute.Attribute;
import vjvm.runtime.classdata.attribute.ConstantValue;
import vjvm.runtime.classdata.constant.ClassRef;
import vjvm.utils.ArrayUtil;
import vjvm.utils.InvokeUtil;
import vjvm.vm.VMContext;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;

import java.io.DataInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

import static vjvm.classfiledefs.ClassAccessFlags.*;
import static vjvm.classfiledefs.FieldDescriptors.*;

public class JClass {
    // inititlized with constructor
    @Getter
    protected JClassLoader classLoader;
    @Getter
    protected String packageName;
    @Getter
    protected short minorVersion;
    @Getter
    protected short majorVersion;
    @Getter
    protected ConstantPool constantPool;
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
    protected int methodAreaIndex;

    // initialized with prepare()
    @Getter
    protected Slots staticFields;
    protected ArrayList<MethodInfo> vtable;
    // size of instance object
    @Getter
    protected int instanceSize;

    @Getter
    @Setter
    protected int initState;
    // the thread calling the initialize method
    private JThread initThread;

    protected JClass() {
    }

    // construct from data
    public JClass(DataInput dataInput, JClassLoader classLoader) {
        this.classLoader = classLoader;
        try {
            // check magic number
            assert dataInput.readInt() == 0xCAFEBABE;
            // parse data
            // skip class version check
            minorVersion = dataInput.readShort();
            majorVersion = dataInput.readShort();

            constantPool = new ConstantPool(dataInput, this);
            accessFlags = dataInput.readShort();
            int thisIndex = dataInput.readUnsignedShort();
            thisClass = (ClassRef) constantPool.constant(thisIndex);
            String name = thisClass.name();
            packageName = name.substring(0, name.lastIndexOf('/'));
            int superIndex = dataInput.readUnsignedShort();
            if (superIndex != 0)
                superClass = (ClassRef) constantPool.constant(superIndex);
            int interfacesCount = dataInput.readUnsignedShort();
            interfaces = new ClassRef[interfacesCount];
            for (int i = 0; i < interfacesCount; ++i) {
                int interfaceIndex = dataInput.readUnsignedShort();
                interfaces[i] = (ClassRef) constantPool.constant(interfaceIndex);
            }
            int fieldsCount = dataInput.readUnsignedShort();
            fields = new FieldInfo[fieldsCount];
            for (int i = 0; i < fieldsCount; ++i)
                fields[i] = new FieldInfo(dataInput, this);
            int methodsCount = dataInput.readUnsignedShort();
            methods = new MethodInfo[methodsCount];
            for (int i = 0; i < methodsCount; ++i)
                methods[i] = new MethodInfo(dataInput, this);
            int attributesCount = dataInput.readUnsignedShort();
            attributes = new Attribute[attributesCount];
            for (int i = 0; i < attributesCount; ++i)
                attributes[i] = Attribute.constructFromData(dataInput, constantPool);
        } catch (IOException e) {
            throw new ClassFormatError();
        }
        methodAreaIndex = VMContext.heap().addJClass(this);
    }

    private static final HashMap<String, JClass> primClasses = new HashMap<>();

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
            superClass.jClass().tryPrepare();
        for (var i : interfaces)
            i.jClass().tryPrepare();
        initState = InitState.PREPARING;
        // create static fields
        int staticSize = 0;
        var staticFieldInfos = Arrays.stream(fields).filter(FieldInfo::static_).collect(Collectors.toList());
        for (var field : staticFieldInfos) {
            field.offset(staticSize);
            staticSize += FieldDescriptors.size(field.descriptor());
        }
        staticFields = new Slots(staticSize);
        // load ConstantValue
        for (var field : staticFieldInfos) {
            if (!field.final_())
                continue;
            for (int i = 0; i < field.attributeCount(); ++i)
                if (field.attribute(i) instanceof ConstantValue) {
                    int offset = field.offset();
                    Object value = ((ConstantValue) field.attribute(i)).value();
                    switch (field.descriptor().charAt(0)) {
                        case DESC_boolean:
                        case DESC_byte:
                        case DESC_char:
                        case DESC_short:
                        case DESC_int:
                            // The stored value is an Integer
                            staticFields.int_(offset, (Integer) value);
                            break;
                        case DESC_float:
                            staticFields.float_(offset, (Float) value);
                            break;
                        case DESC_long:
                            staticFields.long_(offset, (Long) value);
                            break;
                        case DESC_double:
                            staticFields.double_(offset, (Double) value);
                            break;
                    }
                    break;
                }
        }

        // init instance fields
        instanceSize = superClass == null ? 0 : superClass.jClass().instanceSize;
        for (var field : fields) {
            if (field.static_())
                continue;
            field.offset(instanceSize);
            instanceSize += field.size();
        }

        // init vtable
        // copy super's vtable
        if (superClass != null)
            vtable = new ArrayList<>(superClass.jClass().vtable);
        else vtable = new ArrayList<>();
        // insert or override for each method, see spec. 5.4.5
        // transitive overriding is not considered
        // interface methods are not considered, unless they are implemented in super class
        // for other instance methods, they are all added to vtable
        int superlen = vtable.size();
        outer:
        for (var mc : methods) {
            if (mc.static_()) continue;
            for (int j = 0; j < superlen; ++j) {
                var ma = vtable.get(j);
                if (!mc.name().equals(ma.name()) || !mc.descriptor().equals(ma.descriptor()))
                    continue;
                if (mc.private_())
                    continue;
                if (ma.public_() || ma.protected_()
                    || (!ma.private_() && ma.jClass().packageName().equals(packageName))) {
                    vtable.set(j, mc);
                    mc.vtableIndex(j);
                    continue outer;
                }
            }
            // if there is not override found
            vtable.add(mc);
            mc.vtableIndex(vtable.size() - 1);
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
            superClass.jClass().tryInitialize(thread);
        for (var i : interfaces)
            i.jClass().tryInitialize(thread);

        // step8: not doing

        // step9
        // find <clinit>
        MethodInfo clinit = null;
        for (var i : methods)
            if (i.name().equals("<clinit>") && i.descriptor().equals("()V")) {
                clinit = i;
                break;
            }
        if (clinit != null) {
            InvokeUtil.invokeMethodWithArgs(clinit, thread, null);
            VMContext.interpreter().run(thread);
        }

        // step10
        initState = InitState.INITIALIZED;

        // step11,12: not doing

        // debug
        System.err.println(this);
    }

    @Override
    public String toString() {
        return "JClass{" + "thisClass=" + thisClass +
            ", superClass=" + superClass +
            ", interfaces=" + Arrays.toString(interfaces) +
            ", fields=" + Arrays.toString(fields) +
            ", methods=" + Arrays.toString(methods) +
            ", vtable=" + vtable +
            '}';
    }

    /**
     * Finds a field recursively. This is used to resolve field references. See spec. 5.4.3.2
     *
     * @param name       name of the field to find
     * @param descriptor descriptor of the field to find
     * @return the found field, or null if not found
     */
    public FieldInfo findField(String name, String descriptor) {
        for (var field : fields)
            if (field.name().equals(name) && field.descriptor().equals(descriptor))
                return field;
        // find in super interfaces
        for (var si : interfaces) {
            // according to spec. 5.3.5.3, the reference to super interfaces have already been resolved.
            var result = si.jClass().findField(name, descriptor);
            if (result != null)
                return result;
        }
        // then find in super class
        return superClass == null ? null : superClass.jClass().findField(name, descriptor);
    }

    /**
     * Similar to findField, but find in super classes first. See spec. 5.4.3.3
     */
    public MethodInfo findMethod(String name, String descriptor) {
        for (var method : methods)
            if (method.name().equals(name) && method.descriptor().equals(descriptor))
                return method;
        if (superClass != null) {
            var result = superClass.jClass().findMethod(name, descriptor);
            if (result != null)
                return result;
        }
        // Using the rules from JDK7 instead of JDK8
        for (var si : interfaces) {
            var result = si.jClass().findMethod(name, descriptor);
            if (result != null)
                return result;
        }
        return null;
    }

    public ClassRef superInterface(int index) {
        return interfaces[index];
    }

    public int superInterfacesCount() {
        return interfaces.length;
    }

    public Pair<String, JClassLoader> runtimePackage() {
        return Pair.of(packageName, classLoader);
    }

    public MethodInfo vtableMethod(int index) {
        return vtable.get(index);
    }

    /**
     * Check whether this class can be cast to another class. See spec. 6.5.instanceof
     *
     * @param other the class to check against.
     * @return Whether this class can be cast to other.
     */
    public boolean castableTo(JClass other) {
        if (this == other) return true;

        // check for super class and super interfaces
        if (superClass != null && superClass.jClass().castableTo(other))
            return true;
        for (var i : interfaces)
            if (i.jClass().castableTo(other))
                return true;

        // check for array cast
        if (this.array() && other.array())
            return ArrayUtil.componentClass(this).castableTo(
                ArrayUtil.componentClass(other));
        return false;
    }

    /**
     * Check whether this class is a subclass of another class.
     * Interfaces are not taken into account.
     *
     * @param other the class to check against
     * @return Whether this class is a subclass of other. Returns false if this == other.
     */
    public boolean subClassOf(JClass other) {
        return superClass != null
            && (superClass.jClass() == other || superClass.jClass().subClassOf(other));
    }

    public boolean accessibleTo(JClass other) {
        // The accessibility of an array class is the same as its component type. See spec. 5.3.3.2
        if (array()) {
            var elem = ArrayUtil.componentClass(this);
            // workaround: element is primitive type
            if (elem == null) return true;
            return elem.accessibleTo(other);
        }
        return public_() || runtimePackage().equals(other.runtimePackage());
    }

    public boolean public_() {
        return (accessFlags & ACC_PUBLIC) != 0;
    }

    public boolean final_() {
        return (accessFlags & ACC_FINAL) != 0;
    }

    public boolean super_() {
        return (accessFlags & ACC_SUPER) != 0;
    }

    public boolean interface_() {
        return (accessFlags & ACC_INTERFACE) != 0;
    }

    public boolean abstract_() {
        return (accessFlags & ACC_ABSTRACT) != 0;
    }

    public boolean synthetic() {
        return (accessFlags & ACC_SYNTHETIC) != 0;
    }

    public boolean annotation() {
        return (accessFlags & ACC_ANNOTATION) != 0;
    }

    public boolean enum_() {
        return (accessFlags & ACC_ENUM) != 0;
    }

    public boolean module() {
        return (accessFlags & ACC_MODULE) != 0;
    }

    public boolean array() {
        return thisClass.name().charAt(0) == '[';
    }

    public String name() {
        return thisClass.name();
    }

    public int createInstance() {
        assert initState == InitState.INITIALIZED;
        assert !array();
        var heap = VMContext.heap();
        int addr = heap.allocate(instanceSize);

        // set class index
        heap.slots().int_(addr - 1, methodAreaIndex);
        return addr;
    }

    private static int classObject = 0;

    // create a class with all info provided, used to create array and primitive classes.
    public JClass(
        JClassLoader classLoader,
        short minorVersion,
        short majorVersion,
        ConstantPool constantPool,
        short accessFlags,
        ClassRef thisClass,
        ClassRef superClass,
        ClassRef[] interfaces,
        FieldInfo[] fields,
        MethodInfo[] methods,
        Attribute[] attributes) {

        this.classLoader = classLoader;
        this.minorVersion = minorVersion;
        this.majorVersion = majorVersion;

        this.constantPool = constantPool;
        // set class reference in constant pool
        if (constantPool != null)
            constantPool.jClass(this);

        this.accessFlags = accessFlags;

        this.thisClass = thisClass;
        // arrays and primitive classes don't have a package
        this.packageName = name().charAt(0) == DESC_reference ? name().substring(0, name().lastIndexOf('/')) : null;
        this.superClass = superClass;
        this.interfaces = interfaces;

        thisClass.resolve(this);
        if (superClass != null)
            superClass.resolve(this);
        for (var intr : interfaces)
            intr.resolve(this);

        this.fields = fields;
        for (var f : fields)
            f.jClass(this);
        this.methods = methods;
        for (var m : methods)
            m.jClass(this);
        this.attributes = attributes;

        methodAreaIndex = VMContext.heap().addJClass(this);
    }

    public static JClass primitiveClass(String name) {
        var jClass = primClasses.get(name);
        assert jClass != null;
        return jClass;
    }

    static {
        short primAccFlags = ACC_FINAL | ACC_PUBLIC;
        primClasses.put("Z", new JClass(null, (short) 0, (short) 0, null, primAccFlags, new ClassRef("Z"), null, new ClassRef[0], new FieldInfo[0], new MethodInfo[0], null));
        primClasses.put("B", new JClass(null, (short) 0, (short) 0, null, primAccFlags, new ClassRef("B"), null, new ClassRef[0], new FieldInfo[0], new MethodInfo[0], null));
        primClasses.put("C", new JClass(null, (short) 0, (short) 0, null, primAccFlags, new ClassRef("C"), null, new ClassRef[0], new FieldInfo[0], new MethodInfo[0], null));
        primClasses.put("D", new JClass(null, (short) 0, (short) 0, null, primAccFlags, new ClassRef("D"), null, new ClassRef[0], new FieldInfo[0], new MethodInfo[0], null));
        primClasses.put("F", new JClass(null, (short) 0, (short) 0, null, primAccFlags, new ClassRef("F"), null, new ClassRef[0], new FieldInfo[0], new MethodInfo[0], null));
        primClasses.put("I", new JClass(null, (short) 0, (short) 0, null, primAccFlags, new ClassRef("I"), null, new ClassRef[0], new FieldInfo[0], new MethodInfo[0], null));
        primClasses.put("J", new JClass(null, (short) 0, (short) 0, null, primAccFlags, new ClassRef("J"), null, new ClassRef[0], new FieldInfo[0], new MethodInfo[0], null));
        primClasses.put("S", new JClass(null, (short) 0, (short) 0, null, primAccFlags, new ClassRef("S"), null, new ClassRef[0], new FieldInfo[0], new MethodInfo[0], null));
        primClasses.put("boolean", primClasses.get("Z"));
        primClasses.put("byte", primClasses.get("B"));
        primClasses.put("char", primClasses.get("C"));
        primClasses.put("double", primClasses.get("D"));
        primClasses.put("float", primClasses.get("F"));
        primClasses.put("int", primClasses.get("I"));
        primClasses.put("long", primClasses.get("J"));
        primClasses.put("short", primClasses.get("S"));
        for (var c : primClasses.values())
            c.initState(InitState.INITIALIZED);
    }

    /**
     * Get the class object associated with this class.
     *
     * @return an address into the heap slots which points to the class object
     */
    public int classObject() {
        // if the class object has already been initialized
        if (classObject != 0) return classObject;
        JClass classClass;
        try {
            classClass = VMContext.bootstrapLoader().loadClass("java/lang/Class");
        } catch (Exception e) {
            throw new Error(e);
        }
        classObject = classClass.createInstance();
        var slots = VMContext.heap().slots();
        slots.int_(classObject + classClass.instanceSize() - 1, methodAreaIndex);
        return classObject;
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
