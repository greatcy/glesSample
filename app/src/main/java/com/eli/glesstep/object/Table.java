package com.eli.glesstep.object;

import android.opengl.GLES20;

import com.eli.glesstep.Constant;
import com.eli.glesstep.data.VertexArray;
import com.eli.glesstep.programs.TextureShaderProgram;

/**
 * Created by chenjunheng on 2017/9/4.
 * 桌面
 */

public class Table {
    private static final int POSITION_COMPONENT_COUNT = 2;//顶点表示维度
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;//纹理表示维度
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * Constant.BYTES_PER_FLOAT;//跨距

    //顶点数据容器
    private final VertexArray vertexArray;

    //顶点数据+纹理数据
    float[] VERTEX_DATA = {
            // Order of coordinates: X, Y, S,T(X,Y,U,V)

            // Triangle Fan
            0f, 0f, 0.5f, 0.5f,
            -0.5f, -0.8f, 0f, 0.9f,
            0.5f, -0.8f, 1f, 0.9f,
            0.5f, 0.8f, 1f, 0.1f,
            -0.5f, 0.8f, 0f, 0.1f,
            -0.5f, -0.8f, 0f, 0.9f,
    };

    public Table() {
        this.vertexArray = new VertexArray(VERTEX_DATA);
    }

    public void bindData(TextureShaderProgram textureShaderProgram) {
        //设置桌面顶点
        vertexArray.setVertexAttribPointer(0, textureShaderProgram.getPositionLocation(),
                POSITION_COMPONENT_COUNT, STRIDE);

        //设置纹理顶点数据
        vertexArray.setVertexAttribPointer(POSITION_COMPONENT_COUNT,
                textureShaderProgram.getTextureCoordinatesLocation(),
                TEXTURE_COORDINATES_COMPONENT_COUNT,
                STRIDE);
    }

    public void draw() {
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 6);
    }
}
