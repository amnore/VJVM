package vjvm.utils;

import vjvm.runtime.*;
import vjvm.runtime.classdata.MethodInfo;
import vjvm.runtime.object.ArrayObject;
import vjvm.runtime.object.StringObject;
import org.apache.commons.lang3.tuple.Triple;

import java.util.HashMap;
import java.util.function.Consumer;

public class InvokeUtil {
    // (ClassName, MethodName, MethodDescriptor) -> HackFunction
    static HashMap<Triple<String, String, String>, Consumer<JThread>> hackTable;

    static {
        hackTable = new HashMap<>();
        hackTable.put(Triple.of("java/lang/Object", "registerNatives", "()V"), t -> {
        });
        hackTable.put(Triple.of("java/lang/Class", "registerNatives", "()V"), t -> {
        });
        hackTable.put(Triple.of("java/lang/Class", "desiredAssertionStatus0", "(Ljava/lang/Class;)Z"), t -> {
            var s = t.currentFrame().opStack();
            s.popAddress();
            s.pushInt(1);
        });
        hackTable.put(Triple.of("java/lang/String", "intern", "()Ljava/lang/String;"),
            t -> {
                var s = t.currentFrame().opStack();
                var obj = (StringObject) t.context().heap().get(s.popAddress());
                s.pushAddress(obj.intern());
            });
        hackTable.put(Triple.of("java/lang/Throwable", "fillInStackTrace", "(I)Ljava/lang/Throwable;"), t -> {
            var s = t.currentFrame().opStack();
            s.popInt();
        });
        hackTable.put(Triple.of("java/lang/Class", "getPrimitiveClass", "(Ljava/lang/String;)Ljava/lang/Class;"), t -> {
            var s = t.currentFrame().opStack();
            var str = (StringObject) t.context().heap().get(s.popAddress());
            s.pushAddress(t.context().primitiveClass(str.value()).classObject().address());
        });
        hackTable.put(Triple.of("java/lang/Float", "floatToRawIntBits", "(F)I"), t -> {
        });
        hackTable.put(Triple.of("java/lang/Double", "doubleToRawLongBits", "(D)J"), t -> {
        });
        hackTable.put(Triple.of("java/lang/Double", "longBitsToDouble", "(J)D"), t -> {
        });
        hackTable.put(Triple.of("java/lang/System", "registerNatives", "()V"), t -> {
        });
        hackTable.put(Triple.of("java/lang/StrictMath", "sin", "(D)D"), t -> {
            var s = t.currentFrame().opStack();
            s.pushDouble(Math.sin(s.popDouble()));
        });
        hackTable.put(Triple.of("java/lang/StrictMath", "exp", "(D)D"), t -> {
            var s = t.currentFrame().opStack();
            s.pushDouble(Math.exp(s.popDouble()));
        });
        hackTable.put(Triple.of("java/lang/StrictMath", "pow", "(DD)D"), t -> {
            var s = t.currentFrame().opStack();
            var right = s.popDouble();
            var left = s.popDouble();
            s.pushDouble(Math.pow(left, right));
        });
        hackTable.put(Triple.of("java/lang/System", "arraycopy", "(Ljava/lang/Object;ILjava/lang/Object;II)V"), t -> {
            // only support char[], no checks
            var s = t.currentFrame().opStack();
            var heap = t.context().heap();
            var length = s.popInt();
            var destPos = s.popInt();
            var dest = (ArrayObject) heap.get(s.popAddress());
            var srcPos = s.popInt();
            var src = (ArrayObject) heap.get(s.popAddress());
            for (int i = 0; i < length; ++i) {
                dest.char_(destPos + 1, src.char_(srcPos + i));
            }
        });
    }

    public static void invokeMethod(MethodInfo method, JThread thread) {
        var t = Triple.of(method.jClass().thisClass().name(), method.name(), method.descriptor());
        var m = hackTable.get(t);
        if (m != null) {
            m.accept(thread);
            return;
        }

        if (method.native_())
            throw new Error("Unimplemented native method: " + t);

        var stack = thread.currentFrame().opStack();
        var newFrame = new JFrame(method, thread.context());
        var argSlots = method.argc();
        if (!method.static_())
            ++argSlots;

        stack.slots().copyTo(stack.top() - argSlots, argSlots, newFrame.localVars(), 0);
        stack.popSlots(argSlots);
        thread.pushFrame(newFrame);
    }

    /**
     * Invoke a method when there is no frames in a thread.
     *
     * @param method the method to call
     * @param thread the thread to run
     * @param args   the supplied arguments, index begins at 0, can be null if the method has no arguments
     */
    public static void invokeMethodWithArgs(MethodInfo method, JThread thread, Slots args) {
        var frame = new JFrame(method, thread.context());
        var argc = method.argc();
        if (!method.static_())
            ++argc;
        if (argc != 0)
            args.copyTo(0, argc, frame.localVars(), 0);
        thread.pushFrame(new JFrame(method, thread.context()));
    }
}
