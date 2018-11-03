package com.ireader.airhockey.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import static android.opengl.GLES10.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE_BINDING_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLUtils.texImage2D;

/**
 * Created by guohongbin on 2018/11/3.
 */
public class TextureHelper {
    public static final String TAG = "TextureHelper";

    public static int loadTexture(Context context, int resourceId) {
        final int[] textureObjectIds = new int[1];
        // 生成纹理对象
        glGenTextures(1, textureObjectIds, 0);

        if (textureObjectIds[0] == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not generate a new OpenGl texture object.");
            }

            return 0;
        }


        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                resourceId, options);
        if (bitmap == null) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Resource ID " + resourceId + " could not be decoded.");
            }

            glDeleteTextures(1, textureObjectIds, 0);
            return 0;
        }

        // 告诉OPEN GL后面的纹理调用应该应用于这个纹理对象
        glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);

        // 设置过滤器，
        // GL_TEXTURE_MIN_FILTER指缩小的情况，缩小时使用GL_LINEAR_MIPMAP_LINEAR三线性过滤
        // GL_TEXTURE_MAG_FILTER指放大的情况，放大时使用GL_LINEAR双线性过滤
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // 调用位图数据到OPEN GL，并把它复制到当前绑定纹理对象
        texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);

        // 生成MIP贴图
        glGenerateMipmap(GL_TEXTURE_2D);

        // 释放位图数据
        bitmap.recycle();

        // 解除纹理绑定，0表示解除
        glBindTexture(GL_TEXTURE_2D, 0);

        // 返回纹理对象ID
        return textureObjectIds[0];
    }
}
