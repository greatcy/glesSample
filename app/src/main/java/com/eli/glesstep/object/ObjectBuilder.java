package com.eli.glesstep.object;

/**
 * Created by chenjunheng on 2017/9/4.
 * 物体构建者
 */

public class ObjectBuilder {
    private static final int FLOAT_PER_VERTEX = 3;
    private final float[] vertexData;
    private int offset;

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

//    static
}
