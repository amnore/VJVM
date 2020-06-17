package com.mcwcapsule.VJVM;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.ByteBuffer;

import com.mcwcapsule.VJVM.cli.CLI;
import com.mcwcapsule.VJVM.utils.ByteBufferInputStream;
import com.mcwcapsule.VJVM.utils.ByteBufferOutputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import lombok.var;

/**
 * Unit test for CLI.
 */
class CLITest {
    ByteBuffer inBuf = ByteBuffer.allocate(1024);
    ByteBuffer outBuf = ByteBuffer.allocate(1024);
    ByteBuffer errBuf = ByteBuffer.allocate(1024);

    @BeforeEach
    void setupStdio() {
        System.setIn(new ByteBufferInputStream(inBuf));
        System.setOut(new PrintStream(new ByteBufferOutputStream(outBuf)));
        System.setErr(new PrintStream(new ByteBufferOutputStream(errBuf)));
    }

    @Test
    void testHelp() {
        var args = new String[] { "-h", "-cp", "/lib", "VJVM" };
        CLI.main(args);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteBufferInputStream(outBuf)))) {
            assertTrue(reader.readLine().startsWith("usage"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testEmpty() {
        var args = new String[] {};
        CLI.main(args);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteBufferInputStream(outBuf)))) {
            assertTrue(reader.readLine().startsWith("usage"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testMissingClass() {
        var args = new String[] { "-cp", "/usr/" };
        CLI.main(args);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteBufferInputStream(errBuf)))) {
            assertEquals("Main class required.", reader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
