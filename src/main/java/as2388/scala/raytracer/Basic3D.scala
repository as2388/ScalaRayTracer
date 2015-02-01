package as2388.scala.raytracer

class Line (val point: Point, val vector: Vector) {
    def this(pointA: Point, pointB: Point) = this (pointA, (pointB - pointA) asVector() normalize())
}

class Point(val x: Double, val y: Double, val z: Double) extends Object with Magnitude {
    def -(point: Point) = new Point(x - point.x, y - point.y, z - point.z)

    def +(point: Point) = new Point(x + point.x, y + point.y, z + point.z)

    def +(vector: Vector) = new Point(x + vector.x, y + vector.y, z + vector.z)

    def *(constant: Double) = new Point(x * constant, y * constant, z * constant)

    def distanceTo(p: Point) = magnitude(new Vector(this, p))

    def translate(point: Point) = this + point

    def rotateY(angle: Double) = new Point(
        Math.cos(angle) * x + Math.sin(angle) * z,
        y,
        - Math.sin(angle) * x + Math.cos(angle) * z
    )

    def rotateZ(angle: Double) = new Point(
        Math.cos(angle) * x - Math.sin(angle) * y,
        Math.sin(angle) * x + Math.cos(angle) * y,
        z
    )

    def rotateX(angle: Double) = new Point(
        x,
        Math.cos(angle) * y - Math.sin(angle) * z,
        Math.sin(angle) * y + Math.cos(angle) * z
    )

    def asVector() = new Vector(x, y, z)
}

class Vector(val x: Double, val y: Double, val z: Double) extends Object with Magnitude {
    def this(pointA: Point, pointB: Point) = this(pointA.x - pointB.x, pointA.y - pointB.y, pointA.z - pointB.z)

    def normalize(): Vector = *(1 / magnitude(this))

    def *(constant: Double) = new Vector(x * constant, y * constant, z * constant)

    def dot(v: Vector) = x * v.x + y * v.y + z * v.z

    def -(v: Vector) = new Vector(x - v.x, y - v.y, z - v.z)

    def +(v: Vector) = new Vector(x + v.x, y + v.y, z + v.z)

    def cross(v: Vector) = new Vector(y * v.z - z * v.y, -(x * v.z - z * v.x), x * v.y - y * v.x)

    def rotateX(angle: Double) = new Vector(
        x,
        Math.cos(angle) * y - Math.sin(angle) * z,
        Math.sin(angle) * y + Math.cos(angle) * z
    )

    def rotateY(angle: Double) = new Vector(
        Math.cos(angle) * x + Math.sin(angle) * z,
        y,
        - Math.sin(angle) * x + Math.cos(angle) * z
    )

    def rotateZ(angle: Double) = new Vector(
        Math.cos(angle) * x - Math.sin(angle) * y,
        Math.sin(angle) * x + Math.cos(angle) * y,
        z
    )

    def asPoint() = new Point(x, y, z)
}

trait Magnitude {
    def magnitude(v: Vector): Double = magnitude(v.x, v.y, v.z)

    def magnitude(x: Double, y: Double, z: Double) =
        Math sqrt (x * x + y * y + z * z)
}

trait MinMax {
    /**
     * Returns the minimum value of two values.
     * @param a A value to compare
     * @param b The other value to compare
     * @return  The minimum value of a and b
     */
    def min(a: Double, b: Double) =
        if (a < b) a else b

}