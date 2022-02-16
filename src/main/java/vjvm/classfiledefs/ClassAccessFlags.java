package vjvm.classfiledefs;

public class ClassAccessFlags {
	public static final short ACC_PUBLIC = 0x0001;
	public static final short ACC_FINAL = 0x0010;
	public static final short ACC_SUPER = 0x0020;
	public static final short ACC_INTERFACE = 0x0200;
	public static final short ACC_ABSTRACT = 0x0400;
	public static final short ACC_SYNTHETIC = 0x1000;
	public static final short ACC_ANNOTATION = 0x2000;
	public static final short ACC_ENUM = 0x4000;
	public static final short ACC_MODULE = (short) 0x8000;
}
