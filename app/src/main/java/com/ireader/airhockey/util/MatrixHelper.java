package com.ireader.airhockey.util;

/**
 * Created by guohongbin on 2018/11/3.
 */
public class MatrixHelper {

    public static void perspectiveM(float[] m, float fovy, float aspect,
                                    float zNear, float zFar) {
        final float angleInRadians = (float) (fovy * Math.PI / 180.0);

        final float a = (float) (1.0 / Math.tan(angleInRadians / 2.0));
        m[0] = a / aspect;
        m[1] = 0f;
        m[2] = 0f;
        m[3] = 0f;

        m[4] = 0f;
        m[5] = a;
        m[6] = 0f;
        m[7] = 0f;

        m[8] = 0f;
        m[9] = 0f;
        m[10] = -((zFar + zNear) / (zFar - zNear));
        m[11] = -1f;

        m[12] = 0f;
        m[13] = 0f;
        m[14] = -((2f * zFar * zNear) / (zFar - zNear));
        m[15] = 0f;
    }
}
