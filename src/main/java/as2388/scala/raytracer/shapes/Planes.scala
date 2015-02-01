package as2388.scala.raytracer.shapes

import as2388.scala.raytracer._

import scalafx.scene.paint.Color

class Plane(val normal: Vector, val distance: Double,
            val diffusivity: Double, val reflectivity: Double, val color: Color) extends Shape {
    override def color(point: Point): Color = color

    override def closestIntersection(line: Line): IntersectionData = {
        val closestDistance = - (distance + (normal dot (line.point asVector()))) / (normal dot line.vector)

        if (closestDistance < 0) null
        else new IntersectionData(closestDistance,
            ((line.vector scalarMultiply closestDistance) asPoint() addVector (normal scalarMultiply 0.000001)) add line.point,
            normal, this) //0.0001 adds a tiny bit to the normal, to avoid floating point errors affecting shadow computations
    }
}

class CheckeredPlane(normal: Vector, distance: Double, diffusivity: Double, reflectivity: Double,
                     val color1: Color, val color2: Color)
        extends Plane(normal, distance, diffusivity, reflectivity, null) {
    override def color(point: Point): Color = {
        val x = if (point.x >= 0) point.x.toInt else point.x.toInt - 1
        val y = if (point.y >= 0) point.y.toInt else point.y.toInt - 1

        if (((x ^ y) & 8) == 0) color1 else color2
    }
}

class FinitePlane(normal: Vector, distance: Double, diffusivity: Double, reflectivity: Double, color: Color,
                  val vertices: List[Point], val translationOffset: Point = new Point(0, 0, 0))
        extends Plane(normal, distance, diffusivity, reflectivity, color) with Magnitude {
    val TAU = 2 * Math.PI

    override def closestIntersection(line: Line) : IntersectionData = {
        if ((line.vector dot normal) >= 0) null
        else {
            val testLine = new Line(line.point subtract translationOffset, line.vector)
            val closestIntersection = super.closestIntersection(testLine)
            if (closestIntersection == null) null
            else {
                if (insidePlane(closestIntersection.intersectionPoint)) new IntersectionData(
                    closestIntersection.distance,
                    closestIntersection.intersectionPoint add translationOffset,
                    closestIntersection.normal,
                    closestIntersection.shape
                )
                else null
            }
        }
    }

    private def insidePlane(point: Point): Boolean =
        (angleSum(vertices, point) - TAU).abs < 0.0001

    private def angleSum(vertices: List[Point], testPoint: Point): Double = vertices match {
        case Nil => 0
        case v :: Nil => 0
        case v1 :: v2 :: vs =>
            val p1 = v1 subtract testPoint asVector()
            val p2 = v2 subtract testPoint asVector()
            val m1m2 = magnitude(p1) * magnitude(p2)

            if (m1m2 <= 0.00001) TAU
            else Math.acos((p1 dot p2) / m1m2) + angleSum(v2 :: vs, testPoint)
    }
}