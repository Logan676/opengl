package com.ireader.airhockey.datav11;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glVertexAttribPointer;
import static com.ireader.airhockey.Constants.BYTES_PER_FLOAT;

/**
 * Created by guohongbin on 2018/12/20.
 */
public class VertexBuffer {
    private final int bufferId;

    public VertexBuffer(float[] vertexData) {
        final int[] buffers = new int[1];
        glGenBuffers(buffers.length, buffers, 0);
        if (buffers[0] == 0) {
            throw new RuntimeException("Could not create a new vertex buffer object.");
        }

        bufferId = buffers[0];

        glBindBuffer(GL_ARRAY_BUFFER, buffers[0]);

        // Transfer data to native memory
        FloatBuffer vertextArray = ByteBuffer
                .allocateDirect(vertexData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertextArray.position(0);

        // Transfer data from native memory to the GPU buffer
        glBufferData(GL_ARRAY_BUFFER,
                vertextArray.capacity() * BYTES_PER_FLOAT,
                vertextArray,
                GL_STATIC_DRAW);

        // Important:unbind buffer when we`re done with it.
        glBindBuffer(GL_ARRAY_BUFFER, 0);

    }

    public void setVertexAttribPointer(int dataOffset,
                                       int attributeLocation,
                                       int componentCount,
                                       int stride) {
        // GL_ARRAY_BUFFER表示顶点缓冲区对象
        // GL_ELEMENT_ARRAY_BUFFER表示索引缓冲区对象
        glBindBuffer(GL_ARRAY_BUFFER, bufferId);
        glVertexAttribPointer(attributeLocation,
                componentCount,
                GL_FLOAT,
                false,
                stride,
                dataOffset);

        glEnableVertexAttribArray(attributeLocation);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
}
