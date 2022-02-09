package vjvm.runtime;

import lombok.SneakyThrows;
import vjvm.classfiledefs.Descriptors;
import vjvm.classloader.JClassLoader;
import vjvm.runtime.classdata.ConstantPool;
import vjvm.runtime.classdata.FieldInfo;
import vjvm.runtime.classdata.MethodInfo;
import vjvm.runtime.classdata.attribute.Attribute;
import vjvm.runtime.classdata.attribute.ConstantValue;
import vjvm.runtime.classdata.attribute.NestHost;
import vjvm.runtime.classdata.attribute.NestMember;
import vjvm.runtime.classdata.constant.ClassRef;
import vjvm.runtime.classdata.constant.Constant;
import vjvm.runtime.object.ClassObject;
import vjvm.vm.VMContext;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;

import java.io.DataInput;
import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Arrays;

import static vjvm.classfiledefs.ClassAccessFlags.*;
import static vjvm.classfiledefs.Descriptors.*;

public class JClass {
    // inititlized with constructor
    @Getter
    private final JClassLoader classLoader;
    @Getter
    private final String packageName;
    @Getter
    private final int minorVersion;
    @Getter
    private final int majorVersion;
    @Getter
    private final ConstantPool constantPool;
    @Getter
    private final int accessFlags;
    @Getter
    private final ClassRef thisClass;
    @Getter
    private final ClassRef superClass;
    private final ClassRef[] interfaces;
    private final FieldInfo[] fields;
    private final MethodInfo[] methods;
    private final Attribute[] attributes;

    // initialized with prepare()
    @Getter
    private Slots staticFields;
    private ArrayList<MethodInfo> vtable;

    // size of instance object
    @Getter
    private int instanceSize;

    @Getter
    private int initState = InitState.LOADED;

    // the thread calling the initialize method
    private JThread initThread;

    private JClass nestHost;

    private ClassObject classObject;

    // construct from data
    @SneakyThrows
    public JClass(DataInput dataInput, JClassLoader classLoader) {
        this.classLoader = classLoader;

        // check magic number
        var magic = dataInput.readInt();
        if (magic != 0xcafebabe) {
            throw new InvalidClassException(String.format(
                "Wrong magic number, expected: 0xcafebabe, got: 0x%x", magic));
        }

        // parse data
        // skip class version check
        minorVersion = dataInput.readUnsignedShort();
        majorVersion = dataInput.readUnsignedShort();

        constantPool = new ConstantPool(dataInput, this);
        accessFlags = dataInput.readUnsignedShort();
        thisClass = (ClassRef) constantPool.constant(dataInput.readUnsignedShort());
        packageName = name().substring(0, name().lastIndexOf('/'));

        var superIndex = dataInput.readUnsignedShort();
        superClass = superIndex == 0 ? null : (ClassRef) constantPool.constant(superIndex);

        var interfacesCount = dataInput.readUnsignedShort();
        interfaces = new ClassRef[interfacesCount];
        for (var i = 0; i < interfacesCount; ++i) {
            var interfaceIndex = dataInput.readUnsignedShort();
            interfaces[i] = (ClassRef) constantPool.constant(interfaceIndex);
        }

        var fieldsCount = dataInput.readUnsignedShort();
        fields = new FieldInfo[fieldsCount];
        for (var i = 0; i < fieldsCount; ++i)
            fields[i] = new FieldInfo(dataInput, this);

        var methodsCount = dataInput.readUnsignedShort();
        methods = new MethodInfo[methodsCount];
        for (var i = 0; i < methodsCount; ++i)
            methods[i] = new MethodInfo(dataInput, this);

        var attributesCount = dataInput.readUnsignedShort();
        attributes = new Attribute[attributesCount];
        for (var i = 0; i < attributesCount; ++i)
            attributes[i] = Attribute.constructFromData(dataInput, constantPool);

        // Spec. 5.3.3, 5.3.4: resolve super class and interfaces
        // These are delayed to preparation stage
    }

    // create a class with all info provided, used to create array and primitive classes.
    public JClass(
        JClassLoader classLoader,
        short accessFlags,
        String name,
        String superClassName,
        String[] interfaceNames,
        FieldInfo[] fields,
        MethodInfo[] methods) {
        this.classLoader = classLoader;
        this.minorVersion = this.majorVersion = 0;
        this.constantPool = new ConstantPool(new Constant[0], this);
        this.accessFlags = accessFlags;
        this.thisClass = new ClassRef(name, this);

        // arrays and primitive classes don't have a package
        this.packageName = name().charAt(0) == DESC_reference ? name().substring(0, name().lastIndexOf('/')) : null;
        this.superClass = superClassName == null ? null : new ClassRef(superClassName, this);
        this.interfaces = Arrays.stream(interfaceNames)
            .map(n -> new ClassRef(n, this)).toArray(ClassRef[]::new);

        this.fields = fields;
        for (var f : fields)
            f.jClass(this);
        this.methods = methods;
        for (var m : methods)
            m.jClass(this);
        this.attributes = new Attribute[0];
    }

    public void verify() {
        // skip
        initState = InitState.VERIFIED;
    }

    /**
     * Prepares this class for use. See spec. 5.4.2
     * Invoking this method when the class has been prepared has no effect.
     */
    public void prepare() {
        if (initState >= InitState.PREPARED)
            return;
        // first verify
        verify();
        // prepare super classes and super interfaces
        if (superClass != null)
            superClass.value().prepare();
        for (var i : interfaces)
            i.value().prepare();
        initState = InitState.PREPARING;
        // create static fields
        int staticSize = 0;
        var staticFieldInfos = Arrays.stream(fields).filter(FieldInfo::static_).toList();
        for (var field : staticFieldInfos) {
            field.offset(staticSize);
            staticSize += Descriptors.size(field.descriptor());
        }
        staticFields = new Slots(staticSize);
        // load ConstantValue
        for (var field : staticFieldInfos) {
            if (!field.final_())
                continue;
            for (int i = 0; i < field.attributeCount(); ++i)
                if (field.attribute(i) instanceof ConstantValue) {
                    int offset = field.offset();
                    // TODO: Make it more lazy
                    Object value = ((ConstantValue) field.attribute(i)).value();
                    switch (field.descriptor().charAt(0)) {
                        case DESC_boolean, DESC_byte, DESC_char, DESC_short, DESC_int ->
                            // The stored value is an Integer
                            staticFields.int_(offset, (Integer) value);
                        case DESC_float -> staticFields.float_(offset, (Float) value);
                        case DESC_long -> staticFields.long_(offset, (Long) value);
                        case DESC_double -> staticFields.double_(offset, (Double) value);
                    }
                    break;
                }
        }

        // init instance fields
        instanceSize = superClass == null ? 0 : superClass.value().instanceSize;
        for (var field : fields) {
            if (field.static_())
                continue;
            field.offset(instanceSize);
            instanceSize += field.size();
        }

        // init vtable
        // copy super's vtable
        if (superClass != null)
            vtable = new ArrayList<>(superClass.value().vtable);
        else vtable = new ArrayList<>();
        // insert or override for each method, see spec. 5.4.5
        // transitive overriding is not considered
        // interface methods are not considered, unless they are implemented in super class
        // for other instance methods, they are all added to vtable
        int superlen = vtable.size();
        outer:
        for (var m : methods) {
            if (m.static_()) {
                continue;
            }

            for (int j = 0; j < superlen; ++j) {
                var ma = vtable.get(j);
                if (!m.name().equals(ma.name()) || !m.descriptor().equals(ma.descriptor())) {
                    continue;
                }

                if (m.private_()) {
                    continue;
                }

                if (ma.public_() || ma.protected_()
                    || (!ma.private_() && ma.jClass().packageName().equals(packageName))) {
                    vtable.set(j, m);
                    m.vtableIndex(j);
                    continue outer;
                }
            }

            // if there is not override found
            vtable.add(m);
            m.vtableIndex(vtable.size() - 1);
        }

        initState = InitState.PREPARED;
    }

    /**
     * Initialize the class, see spec. 5.5.
     * Invoking this method when the class has been initialized has no effect.
     * The steps correspond to that specified in spec. 5.5.
     *
     */
    public void initialize(JThread thread) {
        assert initState != InitState.ERROR;

        // prepare first
        prepare();

        // step1: there is no LC

        // step2: instead of blocking, just fail
        if (initState == InitState.INITIALIZING && initThread != thread)
            throw new Error();

        // step3
        if (initThread == thread)
            return;

        // step4
        if (initState == InitState.INITIALIZED)
            return;

        // step5: not checking

        // step6
        initState = InitState.INITIALIZING;
        initThread = thread;

        // additional: create a class object in the heap to point to this class
        classObject = new ClassObject(this);

        // step7
        if (superClass != null) {
            superClass.value().initialize(thread);
            if (superClass.value().initState == InitState.ERROR) {
                initState = InitState.ERROR;
                return;
            }
        }

        for (var i : interfaces) {
            i.value().initialize(thread);
            if (i.value().initState == InitState.ERROR) {
                initState = InitState.ERROR;
                return;
            }
        }

        // step8: skip

        // step9
        MethodInfo clinit = findMethod("<clinit>", "()V", true);
        if (clinit != null) {
            context().interpreter().invoke(clinit, thread, new Slots(0));

            if (thread.exception() != null) {
                // TODO: throw exception in thread
                throw new Error("Exception in clinit");
            }
        }

        // step10
        initState = InitState.INITIALIZED;

        // step11,12: skip
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
     * Find a field recursively. This is used to resolve field references. See spec. 5.4.3.2
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
            var result = si.value().findField(name, descriptor);
            if (result != null)
                return result;
        }

        // then find in super class
        return superClass == null ? null : superClass.value().findField(name, descriptor);
    }

    /**
     * Similar to findField, but find in super classes before interfaces. See spec. 5.4.3.3
     */
    public MethodInfo findMethod(String name, String descriptor, boolean thisClassOnly) {
        for (var method : methods)
            if (method.name().equals(name) && method.descriptor().equals(descriptor))
                return method;

        if (thisClassOnly) {
            return null;
        }

        if (superClass != null) {
            var result = superClass.value().findMethod(name, descriptor, false);
            if (result != null)
                return result;
        }

        // Using the rules from JDK7 instead of JDK8
        for (var si : interfaces) {
            var result = si.value().findMethod(name, descriptor, false);
            if (result != null)
                return result;
        }

        return null;
    }

    public <T extends Attribute> T findAttribute(Class<T> attributeType) {
        for (var a: attributes) {
            if (attributeType.isInstance(a)) {
                return (T)a;
            }
        }

        return null;
    }

    public int fieldsCount() {
        return fields.length;
    }

    public FieldInfo field(int index) {
        return fields[index];
    }

    public int methodsCount() {
        return methods.length;
    }

    public MethodInfo method(int index) {
        return methods[index];
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
        if (superClass != null && superClass.value().castableTo(other))
            return true;
        for (var i : interfaces)
            if (i.value().castableTo(other))
                return true;

        // check for array cast
        if (this.array() && other.array())
            return this.component().castableTo(other.component());
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
            && (superClass.value() == other || superClass.value().subClassOf(other));
    }

    public boolean accessibleTo(JClass other) {
        // The accessibility of an array class is the same as its component type. See spec. 5.3.3.2
        if (array()) {
            var elem = this.component();
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
        return name().charAt(0) == '[';
    }

    public String name() {
        return thisClass.name();
    }

    /**
     * Get the class object associated with this class.
     *
     * @return an address into the heap slots which points to the class object
     */
    public ClassObject classObject() {
        assert initState == InitState.INITIALIZED;
        return classObject;
    }

    public JClass nestHost() {
        if (nestHost != null)
            return nestHost;

        var attr = findAttribute(NestHost.class);
        if (attr == null) {
            return nestHost = this;
        }

        JClass host;
        try {
            host = attr.hostClass().value();
        } catch (Exception e) {
            return nestHost = this;
        }

        NestMember m;
        if (!host.runtimePackage().equals(runtimePackage())
        || (m = host.findAttribute(NestMember.class)) == null
        || !m.contains(name())) {
            return nestHost = this;
        }

        return nestHost = host;
    }

    public VMContext context() {
        return classLoader.context();
    }

    private JClass component() {
        assert array();

        var componentType = name().substring(1);
        return classLoader.loadClass(componentType);
    }

    public static class InitState {
        public static final int LOADED = 0;
        public static final int VERIFYING = 1;
        public static final int VERIFIED = 2;
        public static final int PREPARING = 3;
        public static final int PREPARED = 4;
        public static final int INITIALIZING = 5;
        public static final int INITIALIZED = 6;
        public static final int ERROR = 7;
    }
}
