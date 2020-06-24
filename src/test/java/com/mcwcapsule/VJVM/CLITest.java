package com.mcwcapsule.VJVM;

import com.mcwcapsule.VJVM.cli.CLI;
import com.mcwcapsule.VJVM.utils.ByteBufferInputStream;
import com.mcwcapsule.VJVM.utils.ByteBufferOutputStream;
import lombok.var;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for CLI.
 */
class CLITest {
    static ByteBuffer inBuf = ByteBuffer.allocate(1024);
    static ByteBuffer outBuf = ByteBuffer.allocate(1024);
    static ByteBuffer errBuf = ByteBuffer.allocate(1024);

    @AfterAll
    static void cleanup() {
        System.setIn(new FileInputStream(FileDescriptor.in));
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err)));
    }

    @BeforeEach
    void setupStdio() {
        inBuf.clear();
        outBuf.clear();
        errBuf.clear();
        System.setIn(new ByteBufferInputStream(inBuf));
        System.setOut(new PrintStream(new ByteBufferOutputStream(outBuf)));
        System.setErr(new PrintStream(new ByteBufferOutputStream(errBuf)));
    }

    @Test
    void testHelp() {
        var args = new String[]{"-h", "-cp", "/lib", "VJVM"};
        CLI.main(args);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteBufferInputStream(outBuf)))) {
            assertTrue(reader.readLine().startsWith("usage"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testEmpty() {
        var args = new String[]{};
        CLI.main(args);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteBufferInputStream(outBuf)))) {
            assertTrue(reader.readLine().startsWith("usage"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testMissingClass() {
        var args = new String[]{"-cp", "/usr/"};
        CLI.main(args);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteBufferInputStream(errBuf)))) {
            assertEquals("Main class required.", reader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
