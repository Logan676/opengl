package com.ireader.airhockey.objectsv8;

import com.ireader.airhockey.data.VertexArray;
import com.ireader.airhockey.programsv8.ColorShaderProgram;
import com.ireader.airhockey.util.Geometry;

import java.util.List;

/**
 * Created by guohongbin on 2018/11/3.
 */
public class Mallet {
    public static final int POSITION_COMPONENT_COUNT = 3;

    public final float radius;
    public final float height;

    private final VertexArray vertexArray;
    private final List<ObjectBuilder.DrawCommand> drawList;

    public Mallet(float radius, float height, int numPointsAroundMallets) {

        Geometry.Point point = new Geometry.Point(0f, 0f, 0f);

        ObjectBuilder.GeneratedData generatedData = ObjectBuilder.createMallet(
                point, radius, height, numPointsAroundMallets
        );

        this.radius = radius;
        this.height = height;

        vertexArray = new VertexArray(generatedData.vertexData);
        drawList = generatedData.drawList;
    }

    public void bindData(ColorShaderProgram colorProgram) {
        vertexArray.setVertexAttribPointer(0,
                colorProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                0);
    }

    public void draw() {
        for (ObjectBuilder.DrawCommand command : drawList) {
            command.draw();
        }
    }
}
