package com.ireader.airhockey.objectsv10;

import com.ireader.airhockey.data.VertexArray;
import com.ireader.airhockey.programsv10.SkyboxShaderProgram;

import java.nio.ByteBuffer;

import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.GL_VERTEX_ATTRIB_ARRAY_STRIDE;
import static android.opengl.GLES20.glDrawElements;

/**
 * Created by guohongbin on 2018/12/19.
 */
public class Skybox {
    public static final int POSITION_COMPONENT_COUNT = 3;

    private final VertexArray vertexArray;
    private final ByteBuffer indexArray;

    public Skybox() {
        vertexArray = new VertexArray(new float[]{
                -1, 1, 1,
                1, 1, 1,
                -1, -1, 1,
                1, -1, 1,
                -1, 1, -1,
                1, 1, -1,
                -1, -1, -1,
                1, -1, -1
        });

        indexArray = ByteBuffer.allocateDirect(6*6)
        .put(new byte[]{
                1,3,0,
                0,3,2,

                4,6,5,
                5,6,7,

                0,2,4,
                4,2,6,

                5,7,1,
                1,7,3,

                5,1,4,
                4,1,0,

                6,2,7,
                7,2,3

        });

        indexArray.position(0);
    }

    public void bindData(SkyboxShaderProgram skyboxProgram) {
        vertexArray.setVertexAttribPointer(0,
                skyboxProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                0);
    }

    public void draw() {
        glDrawElements(GL_TRIANGLES,36,GL_UNSIGNED_BYTE,indexArray);
    }
}
