package com.ireader.airhockey.datav11;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glVertexAttribPointer;
import static com.ireader.airhockey.Constants.BYTES_PER_FLOAT;
import static com.ireader.airhockey.Constants.BYTES_PER_SHORT;

/**
 * Created by guohongbin on 2018/12/20.
 */
public class IndexBuffer  {
    private final int bufferId;

    public IndexBuffer(short[] indexData) {
        final int[] buffers = new int[1];
        glGenBuffers(buffers.length, buffers, 0);
        if (buffers[0] == 0) {
            throw new RuntimeException("Could not create a new vertex buffer object.");
        }

        bufferId = buffers[0];

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, buffers[0]);

        // Transfer data to native memory
        ShortBuffer indexArray = ByteBuffer
                .allocateDirect(indexData.length * BYTES_PER_SHORT)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
                .put(indexData);
        indexArray.position(0);

        // Transfer data from native memory to the GPU buffer
        glBufferData(GL_ELEMENT_ARRAY_BUFFER,
                indexArray.capacity() * BYTES_PER_SHORT,
                indexArray,
                GL_STATIC_DRAW);

        // Important:unbind buffer when we`re done with it.
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

    }

    public int getBufferId() {
        return bufferId;
    }
}
