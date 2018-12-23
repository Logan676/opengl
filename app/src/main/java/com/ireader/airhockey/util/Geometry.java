package com.ireader.airhockey.util;


/**
 * Created by guohongbin on 2018/11/4.
 */
public class Geometry {


    public static boolean intersects(Sphere sphere, Ray ray) {
        return distanceBetween(sphere.center, ray) < sphere.radius;
    }

    /**
     * 计算射线和平面的交点
     *
     * @param ray
     * @param plane
     * @return
     */
    public static Point intersectionPoint(Ray ray, Plane plane) {
        // 射线到平面的向量
        Vector rayToPlaneVector = vectorBetween(ray.point, plane.point);

        //缩放比例
        float scaleFactor = rayToPlaneVector.dotProduct(plane.normal)
                / ray.vector.dotProduct(plane.normal);

        // 相交的点
        Point intersectionPoint = ray.point.translate(ray.vector.scale(scaleFactor));
        return intersectionPoint;
    }

    // http://mathworld.wolfram.com/Point-LineDistance3-Dimensional.html
    // Note that this formula treats Ray as if it extended infinitely past
    // either point.
    private static float distanceBetween(Point point, Ray ray) {
        Vector p1ToPoint = vectorBetween(ray.point, point);
        Vector p2ToPoint = vectorBetween(ray.point.translate(ray.vector), point);

        // The length of the cross product gives the area of an imaginary
        // parallelogram having the two vectors as sides. A parallelogram can be
        // thought of as consisting of two triangles, so this is the same as
        // twice the area of the triangle defined by the two vectors.
        // http://en.wikipedia.org/wiki/Cross_product#Geometric_meaning
        float areaOfTriangleTimesTwo = p1ToPoint.crossProduct(p2ToPoint).length();
        float lengthOfBase = ray.vector.length();

        // The area of a triangle is also equal to (base * height) / 2. In
        // other words, the height is equal to (area * 2) / base. The height
        // of this triangle is the distance from the point to the ray.

        float distanceFromPointToRay = areaOfTriangleTimesTwo / lengthOfBase;
        return distanceFromPointToRay;
    }

    public static Vector vectorBetween(Point from, Point to) {
        return new Vector(
                to.x - from.x,
                to.y - from.y,
                to.z - from.z);
    }

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

        public Point translate(Vector vector) {
            return new Point(
                    x + vector.x,
                    y + vector.y,
                    z + vector.z
            );
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

    }

    /**
     * 平面上的一个点和一个法向向量
     */
    public static class Plane {
        public final Point point;
        public final Vector normal;

        public Plane(Point point, Vector normal) {
            this.point = point;
            this.normal = normal;
        }
    }

    public static class Ray {
        public final Point point;
        public final Vector vector;

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

        public float length() {
            return (float) Math.sqrt(x * x + y * y + z * z);
        }

        public Vector crossProduct(Vector other) {
            return new Vector(
                    (y * other.z) - (z * other.y),
                    (z * other.x) - (x * other.z),
                    (x * other.y) - (y * other.x));
        }

        public float dotProduct(Vector other) {
            return x * other.x
                    + y * other.y
                    + z * other.z;
        }

        public Vector scale(float f) {
            return new Vector(x * f, y * f, z * f);
        }

        public Vector normalize() {
            return scale(1f / length());
        }
    }

    public static class Sphere {
        public final Point center;
        public final float radius;

        public Sphere(Point center, float radius) {
            this.radius = radius;
            this.center = center;
        }
    }

}
