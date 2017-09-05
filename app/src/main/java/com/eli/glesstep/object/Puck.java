package com.eli.glesstep.object;

import com.eli.glesstep.data.VertexArray;
import com.eli.glesstep.geometry.Geometry;
import com.eli.glesstep.programs.ColorShaderProgram;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenjunheng on 2017/9/5.
 */

public class Puck {
    private static final int POSITION_COMPONENT_COUNT = 3;
    public final float radius, height;

    private final VertexArray vertexArray;
    private final List<ObjectBuilder.DrawCommand> drawList;

    public Puck(float radius, float height, int numPointsAroundPuck) {
        ObjectBuilder.GeneratedData generatedData = ObjectBuilder.createPuck(new Geometry.Cylinder(new Geometry.Point(0f, 0f, 0f), radius, height), numPointsAroundPuck);
        this.radius = radius;
        this.height = height;

        vertexArray = new VertexArray(generatedData.vertexData);
        drawList = generatedData.drawCommands;
    }

    public void bindData(ColorShaderProgram colorShaderProgram) {
        vertexArray.setVertexAttribPointer(0,
                colorShaderProgram.getPositionLocation(),
                POSITION_COMPONENT_COUNT, 0);
    }

    public void draw() {
        for (ObjectBuilder.DrawCommand drawCommand : drawList) {
            drawCommand.draw();
        }
    }
}
