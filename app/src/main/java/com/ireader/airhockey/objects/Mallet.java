package com.ireader.airhockey.objects;

import com.ireader.airhockey.data.VertexArray;
import com.ireader.airhockey.programs.ColorShaderProgram;

import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.glDrawArrays;
import static com.ireader.airhockey.Constants.BYTES_PER_FLOAT;

/**
 * Created by guohongbin on 2018/11/3.
 */
public class Mallet {
    public static final int POSITION_COMPONENT_COUNT = 2;
    public static final int COLOR_COMPONENT_COUNT = 3;
    public static final int STRIDE = (POSITION_COMPONENT_COUNT
            + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    private static final float[] VERTEX_DATA = {
            // Order of Coordinates: X,Y,R,G,B

            // Mallets
            0f, -0.4f, 0f, 0f, 1f,
            0f, 0.4f, 1f, 0f, 0f
    };

    private VertexArray mVertexArray;

    public Mallet() {
        mVertexArray = new VertexArray(VERTEX_DATA);
    }

    public void bindData(ColorShaderProgram colorShaderProgram) {
        mVertexArray.setVertexAttribPointer(0,
                colorShaderProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE);

        mVertexArray.setVertexAttribPointer(POSITION_COMPONENT_COUNT,
                colorShaderProgram.getColorAttributeLocation(),
                COLOR_COMPONENT_COUNT,
                STRIDE);
    }

    public void draw() {
        glDrawArrays(GL_POINTS, 0, 2);
    }
}
