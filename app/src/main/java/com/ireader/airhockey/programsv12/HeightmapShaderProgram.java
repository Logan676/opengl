package com.ireader.airhockey.programsv12;

import android.content.Context;

import com.ireader.airhockey.programsv9.ShaderProgram;
import com.ireader.opengl.R;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform3fv;
import static android.opengl.GLES20.glUniform4fv;
import static android.opengl.GLES20.glUniformMatrix4fv;

/**
 * Created by guohongbin on 2018/12/21.
 */
public class HeightmapShaderProgram extends ShaderProgram {
    private final int uMVMatrixLocation;
    private final int uIT_MV_MatrixLocation;
    private final int uMVPMatrixLocation;
    private final int uPointLightPositionsLocation;
    private final int uPointLightColorsLocation;
    private final int uVectorToLightLocation;

    private final int aPositionLocation;
    private final int aNormalLocation;


    public HeightmapShaderProgram(Context context) {
        super(context, R.raw.heightmap_vertex_shader_v12, R.raw.heightmap_fragment_shader_v12);
        uVectorToLightLocation = glGetUniformLocation(program, U_VECTOR_TO_LIGHT);
        uMVMatrixLocation = glGetUniformLocation(program, U_MV_MATRIX);
        uIT_MV_MatrixLocation = glGetUniformLocation(program, U_IT_MV_MATRIX);
        uMVPMatrixLocation = glGetUniformLocation(program, U_MVP_MATRIX);

        uPointLightPositionsLocation =
                glGetUniformLocation(program, U_POINT_LIGHT_POSITIONS);
        uPointLightColorsLocation =
                glGetUniformLocation(program, U_POINT_LIGHT_COLORS);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aNormalLocation = glGetAttribLocation(program, A_NORMAL);
    }

    public void setUniforms(float[] mvMatrix,
                            float[] it_mvMatrix,
                            float[] mvpMatrix,
                            float[] vectorToDirectionalLight,
                            float[] pointLightPositions,
                            float[] pointLightColors) {
        glUniformMatrix4fv(uMVMatrixLocation, 1, false, mvMatrix, 0);
        glUniformMatrix4fv(uIT_MV_MatrixLocation, 1, false, it_mvMatrix, 0);
        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, mvpMatrix, 0);
        glUniform3fv(uVectorToLightLocation, 1, vectorToDirectionalLight, 0);
        glUniform4fv(uPointLightPositionsLocation, 3, pointLightPositions, 0);
        glUniform3fv(uPointLightColorsLocation, 3, pointLightColors, 0);
    }

    public int getNormalAttributeLocation() {
        return aNormalLocation;
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }
}
