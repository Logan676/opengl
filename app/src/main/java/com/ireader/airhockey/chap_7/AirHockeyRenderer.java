package com.ireader.airhockey.chap_7;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.ireader.airhockey.objects.Mallet;
import com.ireader.airhockey.objects.Table;
import com.ireader.airhockey.programs.ColorShaderProgram;
import com.ireader.airhockey.programs.TextureShaderProgram;
import com.ireader.airhockey.util.TextureHelper;
import com.ireader.opengl.R;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.perspectiveM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

/**
 * Created by guohongbin on 2018/10/27.
 */
public class AirHockeyRenderer implements GLSurfaceView.Renderer {
    private Context context;

    private final float[] projectionMatrix = new float[16];

    private final float[] modelMatrix = new float[16];

    private Table mTable;
    private Mallet mMallet;

    private TextureShaderProgram mTextureShaderProgram;
    private ColorShaderProgram mColorShaderProgram;

    private int texture;

    public AirHockeyRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        glClearColor(0f, 0f, 0f, 0f);

        mTable = new Table();
        mMallet = new Mallet();

        mTextureShaderProgram = new TextureShaderProgram(context);
        mColorShaderProgram = new ColorShaderProgram(context);

        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface);


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

        /*MatrixHelper.perspectiveM(projectionMatrix,
                45,
                (float) (width / height),
                1f, 10f);*/
        perspectiveM(projectionMatrix, 0, 45,
                (float) width / (float) height,
                1f, 10f);

        setIdentityM(modelMatrix, 0);
        /*translateM(modelMatrix, 0, 0f, 0f, -2f);*/
        translateM(modelMatrix, 0, 0f, 0f, -2.5f);
        rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);

        final float[] temp = new float[16];
        multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
        System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);

    }

    /**
     * OnDrawFrame is called whenever a new frame needs to be drawn. Normally,
     * this is done at the refresh rate of the screen.
     */
    @Override
    public void onDrawFrame(GL10 gl10) {
        glClear(GL_COLOR_BUFFER_BIT);

        mTextureShaderProgram.useProgram();
        mTextureShaderProgram.setUniforms(projectionMatrix, texture);
        mTable.bindData(mTextureShaderProgram);
        mTable.draw();

        mColorShaderProgram.useProgram();
        mColorShaderProgram.setUniforms(projectionMatrix);
        mMallet.bindData(mColorShaderProgram);
        mMallet.draw();

    }
}

