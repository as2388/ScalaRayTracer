package as2388.scala.raytracer

class Line (val point: Point, val vector: Vector) {
    def this(pointA: Point, pointB: Point) = this (pointA, (pointB - pointA) asVector() normalize())
}

class Point(val x: Float, val y: Float, val z: Float) extends Object with Magnitude {
    def -(point: Point) = new Point(x - point.x, y - point.y, z - point.z)

    def +(point: Point) = new Point(x + point.x, y + point.y, z + point.z)

    def +(vector: Vector) = new Point(x + vector.x, y + vector.y, z + vector.z)

    def *(constant: Float) = new Point(x * constant, y * constant, z * constant)

    def distanceTo(p: Point) = magnitude(new Vector(this, p))

    def translate(point: Point) = this + point

    def rotateY(angle: Float) = new Point(
        Math.cos(angle).toFloat * x + Math.sin(angle).toFloat * z,
        y,
        - Math.sin(angle).toFloat * x + Math.cos(angle).toFloat * z
    )

    def rotateZ(angle: Float) = new Point(
        Math.cos(angle).toFloat * x - Math.sin(angle).toFloat * y,
        Math.sin(angle).toFloat * x + Math.cos(angle).toFloat * y,
        z
    )

    def rotateX(angle: Float) = new Point(
        x,
        Math.cos(angle).toFloat * y - Math.sin(angle).toFloat * z,
        Math.sin(angle).toFloat * y + Math.cos(angle).toFloat * z
    )

    def asVector() = new Vector(x, y, z)
}

class Vector(val x: Float, val y: Float, val z: Float) extends Object with Magnitude {
    def this(pointA: Point, pointB: Point) = this(pointA.x - pointB.x, pointA.y - pointB.y, pointA.z - pointB.z)

    def normalize(): Vector = *(1 / magnitude(this))

    def *(constant: Float) = new Vector(x * constant, y * constant, z * constant)

    def dot(v: Vector) = x * v.x + y * v.y + z * v.z

    def -(v: Vector) = new Vector(x - v.x, y - v.y, z - v.z)

    def +(v: Vector) = new Vector(x + v.x, y + v.y, z + v.z)

    def cross(v: Vector) = new Vector(y * v.z - z * v.y, -(x * v.z - z * v.x), x * v.y - y * v.x)

    def rotateX(angle: Float) = new Vector(
        x,
        Math.cos(angle).toFloat * y - Math.sin(angle).toFloat * z,
        Math.sin(angle).toFloat * y + Math.cos(angle).toFloat * z
    )

    def rotateY(angle: Float) = new Vector(
        Math.cos(angle).toFloat * x + Math.sin(angle).toFloat * z,
        y,
        - Math.sin(angle).toFloat * x + Math.cos(angle).toFloat * z
    )

    def rotateZ(angle: Float) = new Vector(
        Math.cos(angle).toFloat * x - Math.sin(angle).toFloat * y,
        Math.sin(angle).toFloat * x + Math.cos(angle).toFloat * y,
        z
    )

    def asPoint() = new Point(x, y, z)
}

trait Magnitude {
    def magnitude(v: Vector): Float = magnitude(v.x, v.y, v.z)

    def magnitude(x: Float, y: Float, z: Float): Float =
        (Math sqrt (x * x + y * y + z * z)).toFloat
}

trait MinMax {
    /**
     * Returns the minimum value of two values.
     * @param a A value to compare
     * @param b The other value to compare
     * @return  The minimum value of a and b
     */
    def min(a: Float, b: Float) =
        if (a < b) a else b

}