package com.mcwcapsule.VJVM;

import java.io.OutputStream;
import java.nio.ByteBuffer;

public class ByteBufferOutputStream extends OutputStream {
    ByteBuffer buffer;

    /**
     * Wrap and clear a given buffer.
     * @param buffer the buffer to wrap
     */
    public ByteBufferOutputStream(ByteBuffer buffer) {
        this.buffer = buffer;
        buffer.clear();
    }

    /**
     * Write to the buffer and increase its position.
     */
    @Override
    public void write(int b) {
        buffer.put((byte) b);
    }

}
