package com.ireader.airhockey.chap_2;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.ireader.airhockey.util.LoggerConfig;
import com.ireader.airhockey.util.ShaderHelper;
import com.ireader.airhockey.util.TextResourceReader;
import com.ireader.opengl.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

/**
 * Created by guohongbin on 2018/10/27.
 */
public class AirHockeyRenderer implements GLSurfaceView.Renderer {
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int BYTE_PER_FLOAT = 4;
    private Context context;
    private final FloatBuffer vertexData;
    private int program;
    public static final String U_COLOR = "u_Color";
    public static final String A_POSITION = "a_Position";
    private int uColorLocation;
    private int aPositionLocation;

    public AirHockeyRenderer() {
        // This constructor shouldn't be called -- only kept for showing
        // evolution of the code in the chapter.
        context = null;
        vertexData = null;
    }


    public AirHockeyRenderer(Context context) {
        // This constructor shouldn't be called -- only kept for showing
        // evolution of the code in the chapter.
        this.context = context;
        float[] tableVetices = {
                0f, 0f,
                0f, 14f,
                9f, 14f,
                9f, 0f
        };

//        float[] tableVeticesWithTriangles = {
//                // Triangle1
//                0f, 0f,
//                9f, 14f,
//                0f, 14f,
//                // Triangle2
//                0f, 0f,
//                9f, 0f,
//                9f, 14f,
//                //Line
//                0f, 7f,
//                9f, 7f,
//                //Mallets
//                4.5f, 2f,
//                4.5f, 12f
//        };

        float[] tableVerticesWithTriangles = {
                // Triangle 1
                -0.5f, -0.5f,
                0.5f,  0.5f,
                -0.5f,  0.5f,

                // Triangle 2
                -0.5f, -0.5f,
                0.5f, -0.5f,
                0.5f,  0.5f,

                // Line 1
                -0.5f, 0f,
                0.5f, 0f,

                // Mallets
                0f, -0.25f,
                0f,  0.25f
        };

        vertexData = ByteBuffer.allocateDirect(tableVerticesWithTriangles.length * BYTE_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(tableVerticesWithTriangles);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        // Set the background clear color to red. The first component is
        // red, the second is green, the third is blue, and the last
        // component is alpha, which we don't use in this lesson.
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        final String vertexShaderSource = TextResourceReader
                .readTextFileFromResource(context, R.raw.simple_vertex_shader);
        final String fragmentShaderSource = TextResourceReader
                .readTextFileFromResource(context, R.raw.simple_fragment_shader);

        final int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        final int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);

        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);

        if (LoggerConfig.ON) {
            ShaderHelper.validateProgram(program);
        }

        glUseProgram(program);

        uColorLocation = glGetUniformLocation(program, U_COLOR);

        aPositionLocation = glGetAttribLocation(program, A_POSITION);

        // Bind our data, specified by the variable vertexData, to the vertex
        // attribute at location A_POSITION_LOCATION.
        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT,
                false, 0, vertexData);

        glEnableVertexAttribArray(aPositionLocation);

    }

    /**
     * onSurfaceChanged is called whenever the surface has changed. This is
     * called at least once when the surface is initialized. Keep in mind that
     * Android normally restarts an Activity on rotation, and in that case, the
     * renderer will be destroyed and a new one created.
     *
     * @param width  The new width, in pixels.
     * @param height The new height, in pixels.
     */
    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        // Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height);
    }

    /**
     * OnDrawFrame is called whenever a new frame needs to be drawn. Normally,
     * this is done at the refresh rate of the screen.
     */
    @Override
    public void onDrawFrame(GL10 gl10) {
        // Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT);

        // 更新着色器中的u_Color
        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
        // 绘制桌子
        glDrawArrays(GL_TRIANGLES, 0, 6);

        // 更新着色器中的u_Color
        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        // 绘制一条线
        glDrawArrays(GL_LINES, 6, 2);

        // 更新着色器中的u_Color
        glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
        // 绘制第一个木槌
        glDrawArrays(GL_POINTS, 8, 1);

        // 更新着色器中的u_Color
        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        // 绘制第二个木槌
        glDrawArrays(GL_POINTS, 9, 1);
    }
}

