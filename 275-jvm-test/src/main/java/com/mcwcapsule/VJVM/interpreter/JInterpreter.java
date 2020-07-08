package com.mcwcapsule.VJVM.interpreter;

import com.mcwcapsule.VJVM.interpreter.instruction.Instruction;
import com.mcwcapsule.VJVM.interpreter.instruction.comparisons.*;
import com.mcwcapsule.VJVM.interpreter.instruction.constants.*;
import com.mcwcapsule.VJVM.interpreter.instruction.control.GOTO;
import com.mcwcapsule.VJVM.interpreter.instruction.control.RETURN;
import com.mcwcapsule.VJVM.interpreter.instruction.control.RETURN1S;
import com.mcwcapsule.VJVM.interpreter.instruction.control.RETURN2S;
import com.mcwcapsule.VJVM.interpreter.instruction.conversions.*;
import com.mcwcapsule.VJVM.interpreter.instruction.extended.IFNONNULL;
import com.mcwcapsule.VJVM.interpreter.instruction.extended.IFNULL;
import com.mcwcapsule.VJVM.interpreter.instruction.extended.MULTIANEWARRAY;
import com.mcwcapsule.VJVM.interpreter.instruction.loads.*;
import com.mcwcapsule.VJVM.interpreter.instruction.math.*;
import com.mcwcapsule.VJVM.interpreter.instruction.references.*;
import com.mcwcapsule.VJVM.interpreter.instruction.stack.*;
import com.mcwcapsule.VJVM.interpreter.instruction.stores.*;
import com.mcwcapsule.VJVM.runtime.JThread;
import lombok.val;

public class JInterpreter {
    private final Instruction[] dispatchTable;

    public JInterpreter() {
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
    }

    public void run(JThread thread) {
        // since this method may be invoked when JVM stack isn't empty,
        // for example, by the initialize() method of JClass, we need to exit at the right time.
        int count = thread.getFrameCount();

        while (thread.getFrameCount() >= count) {
            val opcode = Byte.toUnsignedInt(thread.getPC().getByte());
            if (dispatchTable[opcode] == null)
                throw new Error(String.format("Unimplemented: %d", opcode));


            // print debug info
            System.err.println("method: " + thread.getCurrentFrame().getJClass().getThisClass().getName() + ':' + thread.getCurrentFrame().getMethodInfo().getName() + ';' + thread.getCurrentFrame().getMethodInfo().getDescriptor());
            System.err.println(String.format("opcode: %s(%d)", dispatchTable[opcode].getClass().getSimpleName(), opcode));
            System.err.println("local: " + thread.getCurrentFrame().getLocalVars().toString());
            System.err.println("stack: " + thread.getCurrentFrame().getOpStack().toString());
            System.err.println();

            dispatchTable[opcode].fetchAndRun(thread);
        }
    }
}
