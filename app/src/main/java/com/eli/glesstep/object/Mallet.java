package com.eli.glesstep.object;

import android.opengl.GLES20;

import com.eli.glesstep.Constant;
import com.eli.glesstep.data.VertexArray;
import com.eli.glesstep.geometry.Geometry;
import com.eli.glesstep.programs.ColorShaderProgram;

import java.util.List;

/**
 * Created by chenjunheng on 2017/9/4.
 * 木槌
 */

public class Mallet {
    private static final int POSITION_COMPONENT_COUNT = 3;//顶点表示维度

    public final float radius;
    public final float height;
    private final VertexArray vertexArray;
    private final List<ObjectBuilder.DrawCommand> drawCommandList;

    public Mallet(float radius, float height, int numPointsAroundMallet) {
        ObjectBuilder.GeneratedData generatedData = ObjectBuilder.createMallet(new Geometry.Point(0f, 0f, 0f), radius,
                height, numPointsAroundMallet);

        this.radius = radius;
        this.height = height;

        vertexArray = new VertexArray(generatedData.vertexData);
        drawCommandList = generatedData.drawCommands;
    }

    /**
     * 绑定shader
     *
     * @param colorShaderProgram shader封装类
     */
    public void bindData(ColorShaderProgram colorShaderProgram) {
        vertexArray.setVertexAttribPointer(0,
                colorShaderProgram.getPositionLocation(),
                POSITION_COMPONENT_COUNT,
                0);
    }

    public void draw() {
        for (ObjectBuilder.DrawCommand drawCommand : drawCommandList) {
            drawCommand.draw();
        }
    }
}
