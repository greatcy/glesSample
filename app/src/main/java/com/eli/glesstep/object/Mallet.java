package com.eli.glesstep.object;

import android.opengl.GLES20;

import com.eli.glesstep.Constant;
import com.eli.glesstep.data.VertexArray;
import com.eli.glesstep.programs.ColorShaderProgram;

/**
 * Created by chenjunheng on 2017/9/4.
 * 木槌
 */

public class Mallet {
    private static final int POSITION_COMPONENT_COUNT = 2;//顶点表示维度
    private static final int COLOR_COMPONENT_COUNT = 3;//颜色表示维度
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * Constant.BYTES_PER_FLOAT;//跨距

    private static final float[] VERTEX_DATA = {
            //Order of coordinates :x,y,r,g,b
            0f, -0.4f, 0f, 0f, 1f,
            0f, 0.4f, 1f, 0f, 0f
    };

    private final VertexArray vertexArray;

    public Mallet() {
        vertexArray = new VertexArray(VERTEX_DATA);
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
                STRIDE);

        vertexArray.setVertexAttribPointer(POSITION_COMPONENT_COUNT,
                colorShaderProgram.getColorLocation(),
                COLOR_COMPONENT_COUNT,
                STRIDE);
    }

    public void draw() {
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 2);
    }
}
