package com.ireader.airhockey.objectsv8;

import com.ireader.airhockey.data.VertexArray;
import com.ireader.airhockey.programsv8.ColorShaderProgram;
import com.ireader.airhockey.util.Geometry;

import java.util.List;

/**
 * Created by guohongbin on 2018/11/4.
 */
public class Puck {
    public static final int POSITION_COMPONENT_COUNT = 3;
    public final float radius, height;
    private final VertexArray vertexArray;
    private final List<ObjectBuilder.DrawCommand> drawList;

    public Puck(float radius, float height, int numPointsAroundPuck) {
        final Geometry.Point point = new Geometry.Point(0f, 0f, 0f);
        final Geometry.Cylinder cylinder = new Geometry.Cylinder(point, radius, height);
        ObjectBuilder.GeneratedData generatedData =
                ObjectBuilder.createPuck(cylinder, numPointsAroundPuck);

        this.radius = radius;
        this.height = height;

        vertexArray = new VertexArray(generatedData.vertexData);
        drawList = generatedData.drawList;
    }

    public void bindData(ColorShaderProgram colorProgram) {
        vertexArray.setVertexAttribPointer(0,
                colorProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT, 0);

    }

    public void draw() {
        for (ObjectBuilder.DrawCommand drawCommand : drawList) {
            drawCommand.draw();
        }
    }


}
