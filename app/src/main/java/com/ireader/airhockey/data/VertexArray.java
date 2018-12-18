package com.ireader.airhockey.data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;
import static com.ireader.airhockey.Constants.BYTES_PER_FLOAT;

/**
 * Created by guohongbin on 2018/11/3.
 */
public class VertexArray {

    private FloatBuffer floatBuffer;

    public VertexArray(float[] vertexData) {
        floatBuffer = ByteBuffer.allocateDirect(vertexData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
    }

    public void setVertexAttribPointer(int dataOffset, int attributeLocation,
                                       int componentCount, int stride) {
        floatBuffer.position(dataOffset);

        glVertexAttribPointer(attributeLocation,
                componentCount,
                GL_FLOAT,
                false,
                stride,
                floatBuffer);

        glEnableVertexAttribArray(attributeLocation);

        floatBuffer.position(0);
    }

    public void updateBuffer(float[] vertextData, int start, int count) {
        floatBuffer.position(start);
        floatBuffer.put(vertextData, start, count);
        floatBuffer.position(0);
    }



}
