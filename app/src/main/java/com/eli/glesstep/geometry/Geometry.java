package com.eli.glesstep.geometry;

/**
 * Created by chenjunheng on 2017/9/4.
 * 图形类定义
 */

public class Geometry {
    public static class Point {
        public final float x, y, z;

        public Point(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Point translateY(float distance) {
            return new Point(x, y + distance, z);
        }

        /**
         * 返回点根据向量平移之后到达的点
         *
         * @param vector
         * @return
         */
        public Point translate(Vector vector) {
            float x = this.x + vector.x;
            float y = this.y + vector.y;
            float z = this.z + vector.z;
            return new Point(x, y, z);
        }

        /**
         * @param point
         * @return 和另一个点的距离
         */
        public float distance(Point point) {
            return vectorBetween(this, point).length();
        }

        @Override
        public String toString() {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    ", z=" + z +
                    '}';
        }
    }

    public static class Sphere {
        public final Point center;
        public final float radius;

        public Sphere(Point center, float radius) {
            this.center = center;
            this.radius = radius;
        }
    }

    public static class Circle {
        public final Point center;
        public final float radius;

        public Circle(Point center, float radius) {
            this.center = center;
            this.radius = radius;
        }

        public Circle scale(float scale) {
            return new Circle(center, radius * scale);
        }

        @Override
        public String toString() {
            return "Circle{" +
                    "center=" + center +
                    ", radius=" + radius +
                    '}';
        }
    }

    public static class Cylinder {
        public final Point center;
        public final float radius;
        public final float height;

        public Cylinder(Point center, float radius, float height) {
            this.center = center;
            this.radius = radius;
            this.height = height;
        }

        @Override
        public String toString() {
            return "Cylinder{" +
                    "center=" + center +
                    ", radius=" + radius +
                    ", height=" + height +
                    '}';
        }
    }

    public static class Ray {
        public final Point point;
        public final Vector vector;

        /**
         * @param point  射线的起点
         * @param vector 射线的方向
         */
        public Ray(Point point, Vector vector) {
            this.point = point;
            this.vector = vector;
        }
    }

    public static class Vector {
        public final float x, y, z;

        public Vector(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        /**
         * 向量叉乘的运算，结果是垂直于参数的向量，且该结果的长度是两个向量构成的三角形的面积*2
         *
         * @param other
         * @return 返回向量叉乘的结果
         */
        public Vector crossProduct(Vector other) {
            return new Vector(
                    y * other.z - z * other.y,
                    z * other.x - x * other.z,
                    x * other.y - y * other.z);
        }

        /**
         * @return 向量的模
         */
        public float length() {
            return (float) Math.sqrt(x * x + y * y + z * z);
        }
    }

    public static class Plane {
        private final Point center;
        private final Vector normalVector;

        public Plane(Point center, Vector normalVector) {
            this.center = center;
            this.normalVector = normalVector;
        }
    }

    /**
     * @param from
     * @param to
     * @return 两点返回的向量
     */
    public static Vector vectorBetween(Point from, Point to) {
        return new Vector(to.x - from.x,
                to.y - from.y,
                to.z - from.z);
    }

    /**
     * @param sphere
     * @param ray
     * @return 射线和圆是否相交
     */
    public static boolean intersects(Sphere sphere, Ray ray) {
        return distanceBetween(sphere.center, ray) < sphere.radius;
    }

    /**
     * 射线与平面相交检测
     *
     * @param ray
     * @param plane
     * @return 相交点
     */
    public static Point intersects(Ray ray, Plane plane) {
        return null;
    }

    /**
     * @param point
     * @param ray
     * @return 点和射线的距离
     */
    public static float distanceBetween(Point point, Ray ray) {
        Vector p1ToPoint = vectorBetween(ray.point, point);
        Vector p2ToPoint = vectorBetween(point.translate(ray.vector), point);

        Vector crossResult = p1ToPoint.crossProduct(p2ToPoint);

        float triangleBottomLength = vectorBetween(point.translate(ray.vector), ray.point).length();

        //叉乘的模(面积*2)除以底
        return crossResult.length() / triangleBottomLength;
    }

}