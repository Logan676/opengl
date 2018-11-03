package com.ireader.airhockey.programs;

import android.content.Context;

import com.ireader.airhockey.util.ShaderHelper;
import com.ireader.airhockey.util.TextResourceReader;

import static android.opengl.GLES20.glUseProgram;

/**
 * Created by guohongbin on 2018/11/3.
 */
public class ShaderProgram {
    // Uniform constants
    static final String U_MATRIX = "u_Matrix";
    static final String U_TEXTURE_UNIT = "u_TextureUnit";

    // Attribute constants
    static final String A_POSITION = "a_Position";
    static final String A_COLOR = "a_Color";
    static final String A_TEXTURE_COORDINATES = "a_TextureCoordinates";

    // Shader program
    final int program;
    protected ShaderProgram(Context context, int vertexShaderResourceId,
                            int fragmentShaderResourceId) {
        // Compile the shaders and link the program.
        program = ShaderHelper.buildProgram(
                TextResourceReader.readTextFileFromResource(
                        context, vertexShaderResourceId),
                TextResourceReader.readTextFileFromResource(
                        context, fragmentShaderResourceId));
    }

    public void useProgram() {
        // Set the current OpenGL shader program to this program.
        glUseProgram(program);
    }
}
