package vjvm.interpreter;

import org.apache.commons.lang3.tuple.Triple;
import vjvm.classfiledefs.Descriptors;
import vjvm.classfiledefs.MethodDescriptors;
import vjvm.interpreter.instruction.Instruction;
import vjvm.interpreter.instruction.comparisons.*;
import vjvm.interpreter.instruction.constants.*;
import vjvm.interpreter.instruction.control.GOTO;
import vjvm.interpreter.instruction.control.RETURN;
import vjvm.interpreter.instruction.control.RETURN1S;
import vjvm.interpreter.instruction.control.RETURN2S;
import vjvm.interpreter.instruction.conversions.*;
import vjvm.interpreter.instruction.extended.IFNONNULL;
import vjvm.interpreter.instruction.extended.IFNULL;
import vjvm.interpreter.instruction.extended.MULTIANEWARRAY;
import vjvm.interpreter.instruction.loads.*;
import vjvm.interpreter.instruction.math.*;
import vjvm.interpreter.instruction.references.*;
import vjvm.interpreter.instruction.stack.*;
import vjvm.interpreter.instruction.stores.*;
import vjvm.runtime.JFrame;
import vjvm.runtime.JThread;
import vjvm.runtime.Slots;
import vjvm.runtime.classdata.MethodInfo;
import vjvm.runtime.object.ArrayObject;
import vjvm.runtime.object.StringObject;

import java.util.HashMap;
import java.util.function.BiFunction;

import static vjvm.classfiledefs.Descriptors.*;

public class JInterpreter {
    private static final Instruction[] dispatchTable;

    // (ClassName, MethodName, MethodDescriptor) -> HackFunction
    private static final HashMap<Triple<String, String, String>, BiFunction<JThread, Slots, Object>> hackTable = new HashMap<>();

    /**
     * Invoke a method when there is no frames in a thread.
     *
     * @param method the method to call
     * @param thread the thread to run
     * @param args   the supplied arguments, index begins at 0
     */
    public void invoke(MethodInfo method, JThread thread, Slots args) {
        assert args.size() == method.argc() + (method.static_() ? 0 : 1);

        var frame = new JFrame(method, args);
        thread.push(frame);

        if (method.native_()) {
            runNativeMethod(thread);
        } else {
            run(thread);
        }
    }

    private void run(JThread thread) {
        var frame = thread.top();

        while (thread.top() == frame) {
            var opcode = Byte.toUnsignedInt(thread.pc().byte_());
            if (dispatchTable[opcode] == null)
                throw new Error(String.format("Unimplemented: %d", opcode));

            dispatchTable[opcode].fetchAndRun(thread);

            if (thread.exception() != null && !handleException(thread)) {
                thread.pop();
                return;
            }
        }
    }

    private void runNativeMethod(JThread thread) {
        var frame = thread.top();
        var method = frame.method();
        assert method.native_();

        var key = Triple.of(method.jClass().name(), method.name(), method.descriptor());
        var impl = hackTable.get(key);
        if (impl == null) {
            // TODO: throw exception in thread
            throw new Error(String.format("Unimplemented native method: %s", key));
        }

        var ret = impl.apply(thread, frame.vars());
        thread.pop();
        var s = thread.top().stack();

        switch (MethodDescriptors.returnType(method.descriptor())) {
            case 'V':
                break;
            case DESC_array:
            case DESC_reference:
                s.pushAddress((Integer) ret);
                break;
            case DESC_boolean:
                s.pushInt(((Boolean) ret) ? 1 : 0);
                break;
            case DESC_byte:
                s.pushInt((Byte) ret);
                break;
            case DESC_char:
                s.pushInt((Character) ret);
                break;
            case DESC_double:
                s.pushDouble((Double) ret);
                break;
            case DESC_float:
                s.pushFloat((Float) ret);
                break;
            case DESC_int:
                s.pushInt((Integer) ret);
                break;
            case DESC_long:
                s.pushLong((Long) ret);
                break;
            case DESC_short:
                s.pushInt((Short) ret);
                break;
            default:
                throw new Error("Invalid return type");
        }
    }

    private boolean handleException(JThread thread) {
        var frame = thread.top();
        var exception = thread.exception();
        var eClass = exception.type();
        var method = frame.method();
        var pc = frame.pc();
        var stack = frame.stack();

        for (var handler : method.code().exceptionTable()) {
            if (pc.position() < handler.startPC()
                || pc.position() >= handler.endPC()
                || (handler.catchType() != null && !eClass.castableTo(handler.catchType())))
                continue;

            // a matching handler is found
            pc.position(handler.handlerPC());
            stack.clear();
            stack.pushAddress(exception.address());
            thread.exception(null);
            return true;
        }

        return false;
    }

    static {
        // @formatter:off
        dispatchTable = new Instruction[]{
/* 0x00 */  new NOP(), new ACONST_NULL(), new ICONST_X(-1), new ICONST_X(0),
/* 0x04 */  new ICONST_X(1), new ICONST_X(2), new ICONST_X(3), new ICONST_X(4),
/* 0x08 */  new ICONST_X(5), new LCONST_X(0), new LCONST_X(1), new FCONST_X(0),
/* 0x0c */  new FCONST_X(1), new FCONST_X(2), new DCONST_X(0), new DCONST_X(1),
/* 0x10 */  new BIPUSH(), new SIPUSH(), new LDC(), new LDC_W(),
/* 0x14 */  new LDC2_W(), new LOAD1S(), new LOAD2S(), new LOAD1S(),
/* 0x18 */  new LOAD2S(), new LOAD1S(), new LOAD1S_X(0), new LOAD1S_X(1),
/* 0x1c */  new LOAD1S_X(2), new LOAD1S_X(3), new LOAD2S_X(0), new LOAD2S_X(1),
/* 0x20 */  new LOAD2S_X(2), new LOAD2S_X(3), new LOAD1S_X(0), new LOAD1S_X(1),
/* 0x24 */  new LOAD1S_X(2), new LOAD1S_X(3), new LOAD2S_X(0), new LOAD2S_X(1),
/* 0x28 */  new LOAD2S_X(2), new LOAD2S_X(3), new LOAD1S_X(0), new LOAD1S_X(1),
/* 0x2c */  new LOAD1S_X(2), new LOAD1S_X(3), new AIFALOAD(), new LDALOAD(),
/* 0x30 */  new AIFALOAD(), new LDALOAD(), new AIFALOAD(), new BALOAD(),
/* 0x34 */  new CSALOAD(), new CSALOAD(), new STORE1S(), new STORE2S(),
/* 0x38 */  new STORE1S(), new STORE2S(), new STORE1S(), new STORE1S_X(0),
/* 0x3c */  new STORE1S_X(1), new STORE1S_X(2), new STORE1S_X(3), new STORE2S_X(0),
/* 0x40 */  new STORE2S_X(1), new STORE2S_X(2), new STORE2S_X(3), new STORE1S_X(0),
/* 0x44 */  new STORE1S_X(1), new STORE1S_X(2), new STORE1S_X(3), new STORE2S_X(0),
/* 0x48 */  new STORE2S_X(1), new STORE2S_X(2), new STORE2S_X(3), new STORE1S_X(0),
/* 0x4c */  new STORE1S_X(1), new STORE1S_X(2), new STORE1S_X(3), new AIFASTORE(),
/* 0x50 */  new LDASTORE(), new AIFASTORE(), new LDASTORE(), new AIFASTORE(),
/* 0x54 */  new BASTORE(), new CSASTORE(), new CSASTORE(), new POP(),
/* 0x58 */  new POP2(), new DUP(), new DUP2(), new DUP_x1(),
/* 0x5c */  new DUP_x2(), new DUP2_x1(), new DUP2_x2(), new SWAP(),
/* 0x60 */  new IOPR((x, y) -> x + y), new LOPR((x, y) -> x + y), new FOPR((x, y) -> x + y), new DOPR((x, y) -> x + y),
/* 0x64 */  new IOPR((x, y) -> x - y), new LOPR((x, y) -> x - y), new FOPR((x, y) -> x - y), new DOPR((x, y) -> x - y),
/* 0x68 */  new IOPR((x, y) -> x * y), new LOPR((x, y) -> x * y), new FOPR((x, y) -> x * y), new DOPR((x, y) -> x * y),
/* 0x6c */  new IOPR((x, y) -> x / y), new LOPR((x, y) -> x / y), new FOPR((x, y) -> x / y), new DOPR((x, y) -> x / y),
/* 0x70 */  new IOPR((x, y) -> x % y), new LOPR((x, y) -> x % y), new FOPR((x, y) -> x % y), new DOPR((x, y) -> x % y),
/* 0x74 */  new INEG(), new LNEG(), new FNEG(), new DNEG(),
/* 0x78 */  new IOPR((x, y) -> x << y), new LSHL(), new IOPR((x, y) -> x >> y), new LSHR(),
/* 0x7c */  new IOPR((x, y) -> x >>> y), new LUSHR(), new IOPR((x, y) -> x & y), new LOPR((x, y) -> x & y),
/* 0x80 */  new IOPR((x, y) -> x | y), new LOPR((x, y) -> x | y), new IOPR((x, y) -> x ^ y), new LOPR((x, y) -> x ^ y),
/* 0x84 */  new IINC(), new I2L(), new I2F(), new I2D(),
/* 0x88 */  new L2I(), new L2F(), new L2D(), new F2I(),
/* 0x8c */  new F2L(), new F2D(), new D2I(), new D2L(),
/* 0x90 */  new D2F(), new I2B(), new I2C(), new I2S(),
/* 0x94 */  new LCMP(), new FCMPL(), new FCMPG(), new DCMPL(),
/* 0x98 */  new DCMPG(), new IFCOND(x -> x == 0), new IFCOND(x -> x != 0), new IFCOND(x -> x < 0),
/* 0x9c */  new IFCOND(x -> x >= 0), new IFCOND(x -> x > 0), new IFCOND(x -> x <= 0), new IF_ICMPCOND((x, y) -> x == y),
/* 0xa0 */  new IF_ICMPCOND((x, y) -> x != y), new IF_ICMPCOND((x, y) -> x < y), new IF_ICMPCOND((x, y) -> x >= y), new IF_ICMPCOND((x, y) -> x > y),
/* 0xa4 */  new IF_ICMPCOND((x, y) -> x <= y), new IF_ACMPCOND((x, y) -> x == y), new IF_ACMPCOND((x, y) -> x != y), new GOTO(),
/* 0xa8 */  null, null, null, null,
/* 0xac */  new RETURN1S(), new RETURN2S(), new RETURN1S(), new RETURN2S(),
/* 0xb0 */  new RETURN1S(), new RETURN(), new GETSTATIC(), new PUTSTATIC(),
/* 0xb4 */  new GETFIELD(), new PUTFIELD(), new INVOKEVIRTUAL(), new INVOKESPECIAL(),
/* 0xb8 */  new INVOKESTATIC(), new INVOKEINTERFACE(), null, new NEW(),
/* 0xbc */  new NEWARRAY(), new ANEWARRAY(), new ARRAYLENGTH(), new ATHROW(),
/* 0xc0 */  new CHECKCAST(), new INSTANCEOF(), null, null,
/* 0xc4 */  null, new MULTIANEWARRAY(), new IFNULL(), new IFNONNULL(),
/* 0xc8 */  null, null, null, null,
/* 0xcc */  null, null, null, null,
/* 0xd0 */  null, null, null, null,
/* 0xd4 */  null, null, null, null,
/* 0xd8 */  null, null, null, null,
/* 0xdc */  null, null, null, null,
/* 0xe0 */  null, null, null, null,
/* 0xe4 */  null, null, null, null,
/* 0xe8 */  null, null, null, null,
/* 0xec */  null, null, null, null,
/* 0xf0 */  null, null, null, null,
/* 0xf4 */  null, null, null, null,
/* 0xf8 */  null, null, null, null,
/* 0xfc */  null, null, null, null,
        };
        // @formatter:on

        hackTable.put(Triple.of("java/lang/Object", "registerNatives", "()V"), (t, a) -> null);
        hackTable.put(Triple.of("java/lang/Class", "registerNatives", "()V"), (t, a) -> null);
        hackTable.put(Triple.of("java/lang/Class", "desiredAssertionStatus0", "(Ljava/lang/Class;)Z"), (t, a) -> true);
        hackTable.put(Triple.of("java/lang/String", "intern", "()Ljava/lang/String;"),
            (t, a) -> {
                var h = t.context().heap();
                var s = (StringObject) t.context().heap().get(a.address(0));
                return h.intern(s);
            });
        hackTable.put(Triple.of("java/lang/Throwable", "fillInStackTrace", "(I)Ljava/lang/Throwable;"), (t, a) -> a.address(0));
        hackTable.put(Triple.of("java/lang/Class", "getPrimitiveClass", "(Ljava/lang/String;)Ljava/lang/Class;"), (t, a) -> {
            var c = t.context();
            var str = (StringObject) c.heap().get(a.address(0));
            var desc = Descriptors.of(str.value());
            return c.bootstrapLoader().loadClass(desc).classObject().address();
        });
        hackTable.put(Triple.of("java/lang/Float", "floatToRawIntBits", "(F)I"), (t, a) -> a.int_(0));
        hackTable.put(Triple.of("java/lang/Double", "doubleToRawLongBits", "(D)J"), (t, a) -> a.long_(0));
        hackTable.put(Triple.of("java/lang/Double", "longBitsToDouble", "(J)D"), (t, a) -> a.double_(0));
        hackTable.put(Triple.of("java/lang/System", "registerNatives", "()V"), (t, a) -> null);
        hackTable.put(Triple.of("java/lang/StrictMath", "sin", "(D)D"), (t, a) -> Math.sin(a.double_(0)));
        hackTable.put(Triple.of("java/lang/StrictMath", "exp", "(D)D"), (t, a) -> Math.exp(a.double_(0)));
        hackTable.put(Triple.of("java/lang/StrictMath", "pow", "(DD)D"), (t, a) -> Math.pow(a.double_(0), a.double_(2)));
        hackTable.put(Triple.of("java/lang/System", "arraycopy", "(Ljava/lang/Object;ILjava/lang/Object;II)V"), (t, a) -> {
            // only support char[], no checks
            var heap = t.context().heap();
            var src = (ArrayObject) heap.get(a.address(0));
            var srcPos = a.int_(1);
            var dest = (ArrayObject) heap.get(a.address(2));
            var destPos = a.address(3);
            var length = a.int_(4);

            for (int i = 0; i < length; ++i) {
                dest.char_(destPos + 1, src.char_(srcPos + i));
            }
            return null;
        });
    }
}
