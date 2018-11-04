package com.ireader.airhockey.chap_8;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.ireader.airhockey.objectsv8.Mallet;
import com.ireader.airhockey.objectsv8.Puck;
import com.ireader.airhockey.objectsv8.Table;
import com.ireader.airhockey.programsv8.ColorShaderProgram;
import com.ireader.airhockey.programsv8.TextureShaderProgram;
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
import static android.opengl.Matrix.setLookAtM;
import static android.opengl.Matrix.translateM;

/**
 * Created by guohongbin on 2018/10/27.
 */
public class AirHockeyRenderer implements GLSurfaceView.Renderer {
    private Context context;

    private final float[] viewMatrix = new float[16];

    private final float[] projectionMatrix = new float[16];

    private final float[] viewProjectionMatrix = new float[16];

    private final float[] modelViewProjectionMatrix = new float[16];

    private final float[] modelMatrix = new float[16];

    private Table mTable;
    private Mallet mMallet;
    private Puck mPuck;

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
        mMallet = new Mallet(0.08f, 0.15f, 32);
        mPuck = new Puck(0.06f, 0.02f, 32);

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

        setLookAtM(viewMatrix, 0, 0f, 1.2f, 2.2f, 0f, 0f, 0f, 0f, 1f, 0f);
    }

    /**
     * OnDrawFrame is called whenever a new frame needs to be drawn. Normally,
     * this is done at the refresh rate of the screen.
     */
    @Override
    public void onDrawFrame(GL10 gl10) {
        glClear(GL_COLOR_BUFFER_BIT);

        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        positionTableInScene();
        mTextureShaderProgram.useProgram();
        mTextureShaderProgram.setUniforms(modelViewProjectionMatrix, texture);
        mTable.bindData(mTextureShaderProgram);
        mTable.draw();

        positionObjectInScene(0f, mMallet.height / 2f, -0.4f);
        mColorShaderProgram.useProgram();
        mColorShaderProgram.setUniforms(modelViewProjectionMatrix, 1f, 0f, 0f);
        mMallet.bindData(mColorShaderProgram);
        mMallet.draw();

        positionObjectInScene(0f, mMallet.height / 2f, 0.4f);
        mColorShaderProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f);
        mMallet.draw();

        positionObjectInScene(0f, mPuck.height / 2f, 0.f);
        mColorShaderProgram.setUniforms(modelViewProjectionMatrix, 0.8f, 0.8f, 1f);
        mPuck.bindData(mColorShaderProgram);
        mPuck.draw();
    }

    private void positionTableInScene() {
        setIdentityM(modelMatrix, 0);
        rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
    }

    private void positionObjectInScene(float x, float y, float z) {
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, x, y, z);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0);
    }
}


