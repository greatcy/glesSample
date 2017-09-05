package com.eli.glesstep.object;

import android.opengl.GLES20;
import android.util.FloatMath;

import com.eli.glesstep.geometry.Geometry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenjunheng on 2017/9/4.
 * 物体构建者
 */

public class ObjectBuilder {
    //表示顶点的数目
    private static final int FLOAT_PER_VERTEX = 3;
    /**
     * 顶点数据
     */
    private final float[] vertexData;
    /**
     * 偏移值
     */
    private int offset;

    private final List<DrawCommand> drawList = new ArrayList<>();

    /**
     * @param sizeInVerteices 顶点数
     */
    public ObjectBuilder(int sizeInVerteices) {
        this.vertexData = new float[sizeInVerteices * FLOAT_PER_VERTEX];
    }

    /**
     * @param numPoints 实际顶点数
     * @return Circle的圆面顶点数目 (GL 内存顶点表示)
     */
    private static int sizeOfCircleInVerteices(int numPoints) {
        return 1 + (numPoints + 1);
    }

    /**
     * @param numPoints 实际顶点数
     * @return 侧面顶点数(GL 内存顶点表示)
     */
    private static int sizeOfOpenCylinderInVertices(int numPoints) {
        return (numPoints + 1) * 2;
    }

    static GeneratedData createPuck(Geometry.Cylinder puck, int numPoints) {
        //Puck 总点数
        int size = sizeOfCircleInVerteices(numPoints) +
                sizeOfOpenCylinderInVertices(numPoints);

        ObjectBuilder builder = new ObjectBuilder(size);

        //Puck 顶部圆
        Geometry.Circle puckTop = new Geometry.Circle(puck.center.translateY(puck.height / 2f),
                puck.radius);

        builder.appendCircle(puckTop, numPoints);
        builder.appendOpenCylinder(puck, numPoints);

        return builder.build();
    }

    /**
     * 在数组中增加圆数据
     *
     * @param circle    圆
     * @param numPoints 顶点数目(平滑度)
     */
    private void appendCircle(Geometry.Circle circle, int numPoints) {
        final int startVertex = offset / FLOAT_PER_VERTEX;
        final int numVertices = sizeOfCircleInVerteices(numPoints);

        vertexData[offset++] = circle.center.x;
        vertexData[offset++] = circle.center.y;
        vertexData[offset++] = circle.center.z;

        for (int i = 0; i <= numPoints; i++) {
            float angleInRadians = ((float) i / (float) numPoints)
                    * ((float) Math.PI * 2f);

            vertexData[offset++] = circle.center.x + circle.radius * (float) Math.cos(angleInRadians);

            vertexData[offset++] = circle.center.y;
            vertexData[offset++] = circle.center.z + circle.radius * (float) Math.sin(angleInRadians);
        }

        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, startVertex, numVertices);
            }
        });
    }

    private void appendOpenCylinder(Geometry.Cylinder cylinder, int numPoints) {
        final int startVertex = offset / FLOAT_PER_VERTEX;
        final int numVertices = sizeOfCircleInVerteices(numPoints);

        final float yStart = cylinder.center.y - cylinder.height / 2f;
        final float yEnd = cylinder.center.y + cylinder.height / 2f;

        for (int i = 0; i <= numPoints; i++) {
            float angleInRadians = ((float) i / (float) numPoints)
                    * ((float) Math.PI * 2f);

            float xPosition = cylinder.center.x + cylinder.radius * (float) Math.cos(angleInRadians);
            float zPosition = cylinder.center.z + cylinder.radius * (float) Math.sin(angleInRadians);

            vertexData[offset++] = xPosition;
            vertexData[offset++] = yStart;
            vertexData[offset++] = zPosition;

            vertexData[offset++] = xPosition;
            vertexData[offset++] = yEnd;
            vertexData[offset++] = zPosition;
        }

        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, startVertex, numVertices);
            }
        });
    }

    private GeneratedData build() {
        return new GeneratedData(vertexData, drawList);
    }

    /**
     * 绘制命令
     */
    interface DrawCommand {
        void draw();
    }

    static class GeneratedData {
        final float[] vertexData;
        final List<DrawCommand> drawCommands;

        public GeneratedData(float[] vertexData, List<DrawCommand> drawCommands) {
            this.vertexData = vertexData;
            this.drawCommands = drawCommands;
        }
    }
}
