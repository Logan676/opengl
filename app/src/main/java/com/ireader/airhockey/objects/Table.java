package com.ireader.airhockey.objects;

import com.ireader.airhockey.data.VertexArray;
import com.ireader.airhockey.programs.TextureShaderProgram;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;
import static com.ireader.airhockey.Constants.BYTES_PER_FLOAT;

/**
 * Created by guohongbin on 2018/11/3.
 */
public class Table {
    public static final int POSITION_COMPONENT_COUNT = 2;

    public static final int TEXTURE_COORDINATE_COMPONENT_COUNT = 2;

    public static final int STRIDE = (POSITION_COMPONENT_COUNT
            + TEXTURE_COORDINATE_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    private float[] VERTEX_DATA = {
            // Order of Coordinates: X,Y,S,T

            // Triangle Fan
            0f, 0f, 0.5f, 0.5f,
            -0.5f, -0.8f, 0f, 0.9f,
            0.5f, -0.8f, 1.f, 0.9f,
            0.5f, 0.8f, 1.f, 0.1f,
            -0.5f, 0.8f, 0.f, 0.1f,
            -0.5f, -0.8f, 0.f, 0.9f
    };

    private VertexArray vertexArray;

    public Table() {
        vertexArray = new VertexArray(VERTEX_DATA);
    }

    public void bindData(TextureShaderProgram textureShaderProgram) {
        vertexArray.setVertexAttribPointer(0,
                textureShaderProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE);

        vertexArray.setVertexAttribPointer(POSITION_COMPONENT_COUNT,
                textureShaderProgram.getTextureCoordinatesAttributeLocation(),
                TEXTURE_COORDINATE_COMPONENT_COUNT,
                STRIDE);
    }


    public void draw() {
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
    }
}
