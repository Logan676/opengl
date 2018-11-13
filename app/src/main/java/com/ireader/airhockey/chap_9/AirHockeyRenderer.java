package com.ireader.airhockey.chap_9;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.ireader.airhockey.objectsv8.Mallet;
import com.ireader.airhockey.objectsv8.Puck;
import com.ireader.airhockey.objectsv8.Table;
import com.ireader.airhockey.programsv8.ColorShaderProgram;
import com.ireader.airhockey.programsv8.TextureShaderProgram;
import com.ireader.airhockey.util.Geometry;
import com.ireader.airhockey.util.TextureHelper;
import com.ireader.opengl.R;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.invertM;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.perspectiveM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.setLookAtM;
import static android.opengl.Matrix.translateM;
import static com.ireader.airhockey.util.Geometry.Point;
import static com.ireader.airhockey.util.Geometry.Sphere;
import static com.ireader.airhockey.util.Geometry.Ray;
import static com.ireader.airhockey.util.Geometry.Vector;
import static com.ireader.airhockey.util.Geometry.Plane;
import static com.ireader.airhockey.util.Geometry.vectorBetween;

/**
 * Created by guohongbin on 2018/10/27.
 */
public class AirHockeyRenderer implements GLSurfaceView.Renderer {
    private Context context;

    private boolean malletPressed = false;
    private Point blueMalletPosition;
    private Point previousBlueMalletPosition;

    private final float[] invertedViewProjectionMatrix = new float[16];

    private final float[] viewMatrix = new float[16];

    private final float[] projectionMatrix = new float[16];

    private final float[] viewProjectionMatrix = new float[16];

    private final float[] modelViewProjectionMatrix = new float[16];

    private final float[] modelMatrix = new float[16];

    private Table table;
    private Mallet mallet;
    private Puck puck;

    private TextureShaderProgram mTextureShaderProgram;
    private ColorShaderProgram mColorShaderProgram;

    private int texture;

    public final float leftBound = -0.5f;
    public final float rightBound = 0.5f;
    public final float farBound = -0.8f;
    public final float nearBound = 0.8f;

    private Point puckPosition;
    private Vector puckVector;


    public AirHockeyRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        glClearColor(0f, 0f, 0f, 0f);

        table = new Table();
        mallet = new Mallet(0.08f, 0.15f, 32);
        puck = new Puck(0.06f, 0.02f, 32);

        puckPosition = new Point(0f, puck.height / 2f, 0f);
        puckVector = new Vector(0f, 0f, 0f);

        blueMalletPosition = new Point(0f, mallet.height / 2f, 0.4f);

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

        // Translate the puck by its vector
        puckPosition = puckPosition.translate(puckVector);

        // If the puck struck a side, reflect it off that side.
        if (puckPosition.x < leftBound + puck.radius
                || puckPosition.x > rightBound - puck.radius) {
            puckVector = new Vector(-puckVector.x, puckVector.y, puckVector.z);
            puckVector = puckVector.scale(0.9f);
        }

        if (puckPosition.z < farBound + puck.radius
                || puckPosition.z > nearBound - puck.radius) {
            puckVector = new Vector(puckVector.x, puckVector.y, -puckVector.z);
            puckVector = puckVector.scale(0.9f);
        }

        // Clamp the puck position.
        puckPosition = new Point(
                clamp(puckPosition.x, leftBound + puck.radius, rightBound - puck.radius),
                puckPosition.y,
                clamp(puckPosition.z, farBound + puck.radius, nearBound - puck.radius)
        );

        // Friction factor
        puckVector = puckVector.scale(0.99f);

        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        invertM(invertedViewProjectionMatrix, 0, viewProjectionMatrix, 0);

        positionTableInScene();
        mTextureShaderProgram.useProgram();
        mTextureShaderProgram.setUniforms(modelViewProjectionMatrix, texture);
        table.bindData(mTextureShaderProgram);
        table.draw();

        positionObjectInScene(0f, mallet.height / 2f, -0.4f);
        mColorShaderProgram.useProgram();
        mColorShaderProgram.setUniforms(modelViewProjectionMatrix, 1f, 0f, 0f);
        mallet.bindData(mColorShaderProgram);
        mallet.draw();

        positionObjectInScene(blueMalletPosition.x, blueMalletPosition.y, blueMalletPosition.z);
        mColorShaderProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f);
        mallet.draw();

        positionObjectInScene(puckPosition.x, puckPosition.y, puckPosition.z);
        mColorShaderProgram.setUniforms(modelViewProjectionMatrix, 0.8f, 0.8f, 1f);
        puck.bindData(mColorShaderProgram);
        puck.draw();
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

    public void handleTouchPress(float normalizedX, float normalizedY) {
        Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);

        // Now test if this ray intersects with the mallet by creating a
        // bounding sphere that wraps the mallet.
        Sphere malletBoundingSphere = new Sphere(
                new Point(blueMalletPosition.x,
                        blueMalletPosition.y,
                        blueMalletPosition.z),
                mallet.height / 2f);

        // If the ray intersects (if the user touched a part of the screen that
        // intersects the mallet's bounding sphere), then set malletPressed =
        // true.
        malletPressed = Geometry.intersects(malletBoundingSphere, ray);
        Log.d("LM", "handleTouchPress malletPressed=" + malletPressed);
    }

    private Ray convertNormalized2DPointToRay(float normalizedX, float normalizedY) {
        // We'll convert these normalized device coordinates into world-space
        // coordinates. We'll pick a point on the near and far planes, and draw a
        // line between them. To do this transform, we need to first multiply by
        // the inverse matrix, and then we need to undo the perspective divide.

        final float[] nearPointNdc = {normalizedX, normalizedY, -1, 1};
        final float[] farPointNdc = {normalizedX, normalizedY, 1, 1};

        final float[] nearPointWorld = new float[4];
        final float[] farPointWorld = new float[4];

        multiplyMV(nearPointWorld, 0, invertedViewProjectionMatrix, 0, nearPointNdc, 0);
        multiplyMV(farPointWorld, 0, invertedViewProjectionMatrix, 0, farPointNdc, 0);

        divideByW(nearPointWorld);
        divideByW(farPointWorld);

        Point nearPointRay = new Point(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2]);
        Point farPointRay = new Point(farPointWorld[0], farPointWorld[1], farPointWorld[2]);
        return new Ray(nearPointRay,
                vectorBetween(nearPointRay, farPointRay));
    }

    private void divideByW(float[] vector) {
        vector[0] /= vector[3];
        vector[1] /= vector[3];
        vector[2] /= vector[3];
    }

    public void handleTouchDrag(float normalizedX, float normalizedY) {
        Log.d("LM", "handleTouchDrag malletPressed=" + malletPressed);
        if (malletPressed) {
            Ray ray = convertNormalized2DPointToRay(normalizedX, normalizedY);
            // Define a plane representing our air hockey table.
            Plane plane = new Plane(new Point(0, 0, 0), new Vector(0, 1, 0));
            // Find out where the touched point intersects the plane
            // representing our table. We'll move the mallet along this plane.
            Point touchedPoint = Geometry.intersectionPoint(ray, plane);

            /*blueMalletPosition = new Point(touchPoint.x, mallet.height / 2f, touchPoint.z);*/

            previousBlueMalletPosition = blueMalletPosition;

            // Clamp to bounds
            blueMalletPosition = new Point(
                    clamp(touchedPoint.x,
                            leftBound + mallet.radius,
                            rightBound - mallet.radius),
                    mallet.height / 2f,
                    clamp(touchedPoint.z,
                            0f + mallet.radius,
                            nearBound - mallet.radius));

            float distance = Geometry.vectorBetween(blueMalletPosition, puckPosition).length();
            if (distance < (puck.radius + mallet.radius)) {
                // The mallet has struck the puck. Now send the puck flying
                // based on the mallet velocity.
                puckVector = Geometry.vectorBetween(previousBlueMalletPosition, blueMalletPosition);
            }
        }
    }

    public float clamp(float value, float min, float max) {
        return Math.min(max, Math.max(value, min));
    }
}


