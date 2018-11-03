package com.ireader.airhockey.chap_5;

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
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.orthoM;

/**
 * Created by guohongbin on 2018/10/27.
 */
public class AirHockeyRenderer implements GLSurfaceView.Renderer {
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int BYTE_PER_FLOAT = 4;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTE_PER_FLOAT;
    private Context context;
    private final FloatBuffer vertexData;
    private int program;
    private static final String A_POSITION = "a_Position";
    private static final String A_COLOR = "a_Color";
    private static final String U_MATRIX = "u_Matrix";
    private final float[] projectionMatrix = new float[16];
    private int uMatrixLocation;
    private int aColorLocation;
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
                // Order of Coordinates of X,Y,R,G,B

                // Triangle Fan
                0f, 0f, 1.f, 1.f, 1.f,
                -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
                0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
                0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
                -0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
                -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,

                // Line 1
                -0.5f, 0f, 1.f, 0.f, 0.f,
                0.5f, 0f, 1.f, 0.f, 0.f,

                // Mallets
                0f, -0.25f, 0.f, 0.f, 1.f,
                0f, 0.25f, 1.f, 0.f, 0.f
        };

        vertexData = ByteBuffer.allocateDirect(tableVerticesWithTriangles.length * BYTE_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(tableVerticesWithTriangles);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        final String vertexShaderSource = TextResourceReader
                .readTextFileFromResource(context, R.raw.simple_vertex_shader_chap_5);
        final String fragmentShaderSource = TextResourceReader
                .readTextFileFromResource(context, R.raw.simple_fragment_shader_chap_5);

        final int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        final int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);

        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);

        if (LoggerConfig.ON) {
            ShaderHelper.validateProgram(program);
        }

        glUseProgram(program);

        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);

        aPositionLocation = glGetAttribLocation(program, A_POSITION);

        aColorLocation = glGetAttribLocation(program, A_COLOR);

        // Bind our data, specified by the variable vertexData, to the vertex
        // attribute at location A_POSITION_LOCATION.
        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT,
                false, STRIDE, vertexData);

        glEnableVertexAttribArray(aPositionLocation);

        // Bind our data, specified by the variable vertexData, to the vertex
        // attribute at location A_COLOR_LOCATION.
        vertexData.position(POSITION_COMPONENT_COUNT);
        glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT,
                false, STRIDE, vertexData);

        glEnableVertexAttribArray(aColorLocation);

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

        final float aspectRation = width > height ?
                (float) width / (float) height :
                (float) height / (float) width;

        if (width > height) {
            // Landscape
            orthoM(projectionMatrix, 0, -aspectRation, aspectRation, -1, 1, -1, 1);
        } else {
            // Portrait or Square
            orthoM(projectionMatrix, 0, -1.5f, 0.5f, -aspectRation, aspectRation, -1, 1);
        }

    }

    /**
     * OnDrawFrame is called whenever a new frame needs to be drawn. Normally,
     * this is done at the refresh rate of the screen.
     */
    @Override
    public void onDrawFrame(GL10 gl10) {

        // Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT);

        // Assign the matrix
        glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);

        // 绘制桌子
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);

        // 绘制一条线
        glDrawArrays(GL_LINES, 6, 2);

        // 绘制第一个木槌
        glDrawArrays(GL_POINTS, 8, 1);

        // 绘制第二个木槌
        glDrawArrays(GL_POINTS, 9, 1);
    }
}

