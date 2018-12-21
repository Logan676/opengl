package com.ireader.airhockey.objectsv11;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.ireader.airhockey.datav11.IndexBuffer;
import com.ireader.airhockey.datav11.VertexBuffer;
import com.ireader.airhockey.programsv11.HeightmapShaderProgram;

import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_SHORT;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glDrawElements;

/**
 * 每个顶点对应一个像素。
 * 每个顶点都有一个基于其所在图像中的位置和一个基于像素亮度的高度。
 * <p>
 * Created by guohongbin on 2018/12/21.
 */
public class HeightMap {
    public static final int POSITION_COMPONENT_COUNT = 3;

    private final int width;
    private final int height;
    private final int numElements;
    private final VertexBuffer vertexBuffer;
    private final IndexBuffer indexBuffer;

    public HeightMap(Bitmap bitmap) {
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        if (width * height > 65536) {
            throw new RuntimeException("HeightMap is too large for the index buffer");
        }

        numElements = calculateNumElements();
        vertexBuffer = new VertexBuffer(loadBitmapData(bitmap));
        indexBuffer = new IndexBuffer(createIndexData());
    }

    private int calculateNumElements() {
        return (width - 1) * (height - 1) * 2 * 3;
    }

    private float[] loadBitmapData(Bitmap bitmap) {
        final int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        bitmap.recycle();
        final float[] heightMapVertices = new float[width * height * POSITION_COMPONENT_COUNT];
        int offset = 0;

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                final float xPosition = (float) col / (float) (width - 1) - 0.5f;
                final float yPosition =
                        (float) Color.red(pixels[(row * height) + col]) / (float) 255;
                final float zPosition = (float) row / (float) (height - 1) - 0.5f;
                heightMapVertices[offset++] = xPosition;
                heightMapVertices[offset++] = yPosition;
                heightMapVertices[offset++] = zPosition;
            }
        }

        return heightMapVertices;
    }

    private short[] createIndexData() {
        final short[] indexData = new short[numElements];
        int offset = 0;
        for (int row = 0; row < height - 1; row++) {
            for (int col = 0; col < width - 1; col++) {
                short topLeftIndexNum = (short) (row * width + col);
                short topRightIndexNum = (short) (row * width + col + 1);
                short bottomLeftIndexNum = (short) ((row + 1) * width + col);
                short bottomRightIndexNum = (short) ((row + 1) * width + col + 1);

                indexData[offset++] = topLeftIndexNum;
                indexData[offset++] = bottomLeftIndexNum;

                indexData[offset++] = topRightIndexNum;

                indexData[offset++] = topRightIndexNum;
                indexData[offset++] = bottomLeftIndexNum;
                indexData[offset++] = bottomRightIndexNum;
            }
        }

        return indexData;
    }

    public void bindData(HeightmapShaderProgram heightmapProgram) {
        vertexBuffer.setVertexAttribPointer(0, heightmapProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                0);
    }

    public void draw() {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer.getBufferId());
        glDrawElements(GL_TRIANGLES, numElements, GL_UNSIGNED_SHORT, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    }
}
