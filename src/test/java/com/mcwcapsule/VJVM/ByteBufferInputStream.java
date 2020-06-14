package com.mcwcapsule.VJVM;

import java.io.InputStream;
import java.nio.ByteBuffer;

public class ByteBufferInputStream extends InputStream {
    ByteBuffer buffer;
    int offset = 0;

    /**
     * Wrap a given buffer. Read from beginning to its current position.
     * @param buffer the buffer to wrap
     */
    public ByteBufferInputStream(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public int read() {
        if (offset == buffer.position())
            return -1;
        return buffer.get(offset++);
    }
}
