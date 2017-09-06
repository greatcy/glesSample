package com.eli.glesstep.object;

import com.eli.glesstep.data.VertexArray;
import com.eli.glesstep.geometry.Geometry;
import com.eli.glesstep.programs.ColorShaderProgram;

import java.util.List;

/**
 * Created by chenjunheng on 2017/9/6.
 * debug状态下使用的射线
 */

public class DebugRayLine {
    private static final int POSITION_COMPONENT_COUNT = 3;//顶点表示维度
    private final VertexArray vertexArray;
    private final List<ObjectBuilder.DrawCommand> drawCommandList;
    private Geometry.Point pointStart;
    private Geometry.Vector direct;


    public DebugRayLine(Geometry.Ray ray) {
        this.pointStart = ray.point;
        this.direct = ray.vector;

        ObjectBuilder.GeneratedData generatedData = ObjectBuilder.createLine(pointStart, direct);
        vertexArray = new VertexArray(generatedData.vertexData);
        drawCommandList = generatedData.drawCommands;
    }

    /**
     * 绑定shader
     * 设置GL状态机
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
