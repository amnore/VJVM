package custom;

import com.njuse.jvmfinal.Starter;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class ObjTest {
    ByteArrayOutputStream outBytes = null;
    String lineSeparator = System.lineSeparator();
    String cp = String.join(File.separator, "src", "test", "java");

    @org.junit.Before
    public void setUp() {
        outBytes = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outBytes));
    }

    @Test
    public void test1() {
        Starter.runTest("cases/custom/Obj1", cp);
        String out = outBytes.toString().replaceAll("\\s", "");
        assertEquals(out, "346");
    }

    @Test
    public void test2() {
        Starter.runTest("cases/custom/Obj2", cp);
        String out = outBytes.toString().replaceAll("\\s", "");
        assertEquals(out, "13");
    }
}
