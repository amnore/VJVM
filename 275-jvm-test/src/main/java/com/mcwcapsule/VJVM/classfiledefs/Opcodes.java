package com.mcwcapsule.VJVM.classfiledefs;

/**
 * Opcodes in JVM. See spec. 7
 */
public class Opcodes {
    // constants
    public static final byte OPC_nop = 0x00;
    public static final byte OPC_aconst_null = 0x01;
    public static final byte OPC_iconst_m1 = 0x02;
    public static final byte OPC_iconst_0 = 0x03;
    public static final byte OPC_iconst_1 = 0x04;
    public static final byte OPC_iconst_2 = 0x05;
    public static final byte OPC_iconst_3 = 0x06;
    public static final byte OPC_iconst_4 = 0x07;
    public static final byte OPC_iconst_5 = 0x08;
    public static final byte OPC_lconst_0 = 0x09;
    public static final byte OPC_lconst_1 = 0x0a;
    public static final byte OPC_fconst_0 = 0x0b;
    public static final byte OPC_fconst_1 = 0x0c;
    public static final byte OPC_fconst_2 = 0x0d;
    public static final byte OPC_dconst_0 = 0x0e;
    public static final byte OPC_dconst_1 = 0x0f;
    public static final byte OPC_bipush = 0x10;
    public static final byte OPC_sipush = 0x11;
    public static final byte OPC_ldc = 0x12;
    public static final byte OPC_ldc_w = 0x13;
    public static final byte OPC_ldc2_w = 0x14;

    // loads
    public static final byte OPC_iload = 0x15;
    public static final byte OPC_lload = 0x16;
    public static final byte OPC_fload = 0x17;
    public static final byte OPC_dload = 0x18;
    public static final byte OPC_aload = 0x19;
    public static final byte OPC_iload_0 = 0x1a;
    public static final byte OPC_iload_1 = 0x1b;
    public static final byte OPC_iload_2 = 0x1c;
    public static final byte OPC_iload_3 = 0x1d;
    public static final byte OPC_lload_0 = 0x1e;
    public static final byte OPC_lload_1 = 0x1f;
    public static final byte OPC_lload_2 = 0x20;
    public static final byte OPC_lload_3 = 0x21;
    public static final byte OPC_fload_0 = 0x22;
    public static final byte OPC_fload_1 = 0x23;
    public static final byte OPC_fload_2 = 0x24;
    public static final byte OPC_fload_3 = 0x25;
    public static final byte OPC_dload_0 = 0x26;
    public static final byte OPC_dload_1 = 0x27;
    public static final byte OPC_dload_2 = 0x28;
    public static final byte OPC_dload_3 = 0x29;
    public static final byte OPC_aload_0 = 0x2a;
    public static final byte OPC_aload_1 = 0x2b;
    public static final byte OPC_aload_2 = 0x2c;
    public static final byte OPC_aload_3 = 0x2d;
    public static final byte OPC_iaload = 0x2e;
    public static final byte OPC_laload = 0x2f;
    public static final byte OPC_faload = 0x30;
    public static final byte OPC_daload = 0x31;
    public static final byte OPC_aaload = 0x32;
    public static final byte OPC_baload = 0x33;
    public static final byte OPC_caload = 0x34;
    public static final byte OPC_saload = 0x35;

    // stores
    public static final byte OPC_istore = 0x36;
    public static final byte OPC_lstore = 0x37;
    public static final byte OPC_fstore = 0x38;
    public static final byte OPC_dstore = 0x39;
    public static final byte OPC_astore = 0x3a;
    public static final byte OPC_istore_0 = 0x3b;
    public static final byte OPC_istore_1 = 0x3c;
    public static final byte OPC_istore_2 = 0x3d;
    public static final byte OPC_istore_3 = 0x3e;
    public static final byte OPC_lstore_0 = 0x3f;
    public static final byte OPC_lstore_1 = 0x40;
    public static final byte OPC_lstore_2 = 0x41;
    public static final byte OPC_lstore_3 = 0x42;
    public static final byte OPC_fstore_0 = 0x43;
    public static final byte OPC_fstore_1 = 0x44;
    public static final byte OPC_fstore_2 = 0x45;
    public static final byte OPC_fstore_3 = 0x46;
    public static final byte OPC_dstore_0 = 0x47;
    public static final byte OPC_dstore_1 = 0x48;
    public static final byte OPC_dstore_2 = 0x49;
    public static final byte OPC_dstore_3 = 0x4a;
    public static final byte OPC_astore_0 = 0x4b;
    public static final byte OPC_astore_1 = 0x4c;
    public static final byte OPC_astore_2 = 0x4d;
    public static final byte OPC_astore_3 = 0x4e;
    public static final byte OPC_iastore = 0x4f;
    public static final byte OPC_lastore = 0x50;
    public static final byte OPC_fastore = 0x51;
    public static final byte OPC_dastore = 0x52;
    public static final byte OPC_aastore = 0x53;
    public static final byte OPC_bastore = 0x54;
    public static final byte OPC_castore = 0x55;
    public static final byte OPC_sastore = 0x56;

    // stack
    public static final byte OPC_pop = 0x57;
    public static final byte OPC_pop2 = 0x58;
    public static final byte OPC_dup = 0x59;
    public static final byte OPC_dup_x1 = 0x5a;
    public static final byte OPC_dup_x2 = 0x5b;
    public static final byte OPC_dup2 = 0x5c;
    public static final byte OPC_dup2_x1 = 0x5d;
    public static final byte OPC_dup2_x2 = 0x5e;
    public static final byte OPC_swap = 0x5f;

    // math
    public static final byte OPC_iadd = 0x60;
    public static final byte OPC_ladd = 0x61;
    public static final byte OPC_fadd = 0x62;
    public static final byte OPC_dadd = 0x63;
    public static final byte OPC_isub = 0x64;
    public static final byte OPC_lsub = 0x65;
    public static final byte OPC_fsub = 0x66;
    public static final byte OPC_dsub = 0x67;
    public static final byte OPC_imul = 0x68;
    public static final byte OPC_lmul = 0x69;
    public static final byte OPC_fmul = 0x6a;
    public static final byte OPC_dmul = 0x6b;
    public static final byte OPC_idiv = 0x6c;
    public static final byte OPC_ldiv = 0x6d;
    public static final byte OPC_fdiv = 0x6e;
    public static final byte OPC_ddiv = 0x6f;
    public static final byte OPC_irem = 0x70;
    public static final byte OPC_lrem = 0x71;
    public static final byte OPC_frem = 0x72;
    public static final byte OPC_drem = 0x73;
    public static final byte OPC_ineg = 0x74;
    public static final byte OPC_lneg = 0x75;
    public static final byte OPC_fneg = 0x76;
    public static final byte OPC_dneg = 0x77;
    public static final byte OPC_ishl = 0x78;
    public static final byte OPC_lshl = 0x79;
    public static final byte OPC_ishr = 0x7a;
    public static final byte OPC_lshr = 0x7b;
    public static final byte OPC_iushr = 0x7c;
    public static final byte OPC_lushr = 0x7d;
    public static final byte OPC_iand = 0x7e;
    public static final byte OPC_land = 0x7f;
    public static final byte OPC_ior = (byte) 0x80;
    public static final byte OPC_lor = (byte) 0x81;
    public static final byte OPC_ixor = (byte) 0x82;
    public static final byte OPC_lxor = (byte) 0x83;
    public static final byte OPC_iinc = (byte) 0x84;

    // conversions
    public static final byte OPC_i2l = (byte) 0x85;
    public static final byte OPC_i2f = (byte) 0x86;
    public static final byte OPC_i2d = (byte) 0x87;
    public static final byte OPC_l2i = (byte) 0x88;
    public static final byte OPC_l2f = (byte) 0x89;
    public static final byte OPC_l2d = (byte) 0x8a;
    public static final byte OPC_f2i = (byte) 0x8b;
    public static final byte OPC_f2l = (byte) 0x8c;
    public static final byte OPC_f2d = (byte) 0x8d;
    public static final byte OPC_d2i = (byte) 0x8e;
    public static final byte OPC_d2l = (byte) 0x8f;
    public static final byte OPC_d2f = (byte) 0x90;
    public static final byte OPC_i2b = (byte) 0x91;
    public static final byte OPC_i2c = (byte) 0x92;
    public static final byte OPC_i2s = (byte) 0x93;

    // comparisons
    public static final byte OPC_lcmp = (byte) 0x94;
    public static final byte OPC_fcmpl = (byte) 0x95;
    public static final byte OPC_fcmpg = (byte) 0x96;
    public static final byte OPC_dcmpl = (byte) 0x97;
    public static final byte OPC_dcmpg = (byte) 0x98;
    public static final byte OPC_ifeq = (byte) 0x99;
    public static final byte OPC_ifne = (byte) 0x9a;
    public static final byte OPC_iflt = (byte) 0x9b;
    public static final byte OPC_ifge = (byte) 0x9c;
    public static final byte OPC_ifgt = (byte) 0x9d;
    public static final byte OPC_ifle = (byte) 0x9e;
    public static final byte OPC_if_icmpeq = (byte) 0x9f;
    public static final byte OPC_if_icmpne = (byte) 0xa0;
    public static final byte OPC_if_icmplt = (byte) 0xa1;
    public static final byte OPC_if_icmpge = (byte) 0xa2;
    public static final byte OPC_if_icmpgt = (byte) 0xa3;
    public static final byte OPC_if_icmple = (byte) 0xa4;
    public static final byte OPC_if_acmpeq = (byte) 0xa5;
    public static final byte OPC_if_acmpne = (byte) 0xa6;

    // control
    public static final byte OPC_goto = (byte) 0xa7;
    public static final byte OPC_jsr = (byte) 0xa8;
    public static final byte OPC_ret = (byte) 0xa9;
    public static final byte OPC_tableswitch = (byte) 0xaa;
    public static final byte OPC_lookupswitch = (byte) 0xab;
    public static final byte OPC_ireturn = (byte) 0xac;
    public static final byte OPC_lreturn = (byte) 0xad;
    public static final byte OPC_freturn = (byte) 0xae;
    public static final byte OPC_dreturn = (byte) 0xaf;
    public static final byte OPC_areturn = (byte) 0xb0;
    public static final byte OPC_return = (byte) 0xb1;

    // references
    public static final byte OPC_getstatic = (byte) 0xb2;
    public static final byte OPC_putstatic = (byte) 0xb3;
    public static final byte OPC_getfield = (byte) 0xb4;
    public static final byte OPC_putfield = (byte) 0xb5;
    public static final byte OPC_invokevirtual = (byte) 0xb6;
    public static final byte OPC_invokespecial = (byte) 0xb7;
    public static final byte OPC_invokestatic = (byte) 0xb8;
    public static final byte OPC_invokeinterface = (byte) 0xb9;
    public static final byte OPC_invokedynamic = (byte) 0xba;
    public static final byte OPC_new = (byte) 0xbb;
    public static final byte OPC_newarray = (byte) 0xbc;
    public static final byte OPC_anewarray = (byte) 0xbd;
    public static final byte OPC_arraylength = (byte) 0xbe;
    public static final byte OPC_athrow = (byte) 0xbf;
    public static final byte OPC_checkcast = (byte) 0xc0;
    public static final byte OPC_instanceof = (byte) 0xc1;
    public static final byte OPC_monitorenter = (byte) 0xc2;
    public static final byte OPC_monitorexit = (byte) 0xc3;

    // extend
    public static final byte OPC_wide = (byte) 0xc4;
    public static final byte OPC_multianewarray = (byte) 0xc5;
    public static final byte OPC_ifnull = (byte) 0xc6;
    public static final byte OPC_ifnonnull = (byte) 0xc7;
    public static final byte OPC_goto_w = (byte) 0xc8;
    public static final byte OPC_jsr_w = (byte) 0xc9;

    // reserved
    public static final byte OPC_breakpoint = (byte) 0xca;
    public static final byte OPC_impdep1 = (byte) 0xfe;
    public static final byte OPC_impdep2 = (byte) 0xff;
}
