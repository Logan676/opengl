package com.ireader.airhockey.programsv11;

import android.content.Context;

import com.ireader.airhockey.programsv9.ShaderProgram;
import com.ireader.opengl.R;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Created by guohongbin on 2018/12/21.
 */
public class HeightmapShaderProgram extends ShaderProgram {
    private final int uMatrixLocation;
    private final int aPositionLocation;


    public HeightmapShaderProgram(Context context) {
        super(context, R.raw.heightmap_vertex_shader, R.raw.heightmap_fragment_shader);
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }

    public void setUniforms(float[] matrix) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);
    }
}
