package com.eli.glesstep.data;

import android.opengl.GLES20;

import com.eli.glesstep.Constant;
import com.eli.glesstep.renderer.AirHockeyRenderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by chenjunheng on 2017/9/4.
 * 定点数据类
 */

public class VertexArray {
    private final FloatBuffer floatBuffer;

    public VertexArray(float vertexData[]) {
        floatBuffer = ByteBuffer.allocateDirect(vertexData.length *
                Constant.BYTES_PER_FLOAT).
                order(ByteOrder.nativeOrder()).asFloatBuffer().
                put(vertexData);
    }

    /**
     * 这里实际设置里GLES的状态机，表示接下来的绘制都在此状态下进行
     * 设置顶点Opengl 数据属性
     * @param dataOffset 偏移值
     * @param attributeLocation 属性ID
     * @param componentCount 一个组件占用的数值
     * @param stride 跨距
     */
    public void setVertexAttribPointer(int dataOffset, int attributeLocation,
                                       int componentCount, int stride) {
        floatBuffer.position(dataOffset);
        GLES20.glVertexAttribPointer(attributeLocation, componentCount,
                GLES20.GL_FLOAT, false, stride, floatBuffer);
        GLES20.glEnableVertexAttribArray(attributeLocation);
        floatBuffer.position(0);
    }
}
