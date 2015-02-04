package as2388.scala.raytracer.shapes

import as2388.scala.raytracer._

class Plane(val normal: Vector, val distance: Double,
            val diffusivity: Double, val reflectivity: Double, val color: Color) extends Shape {
    override def color(point: Point): Color = color

    override def closestIntersection(line: Line): IntersectionData = {
        val closestDistance = - (distance + (normal dot (line.point asVector()))) / (normal dot line.vector)

        if (closestDistance < 0) null
        else new IntersectionData(closestDistance,
            (((line.vector * closestDistance) asPoint()) + (normal * 0.000001)) + line.point,
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

    def this(vertices: List[Point], diffusivity: Double, reflectivity: Double, color: Color,
             translationOffset: Point) =
        this(
            (new Vector(vertices(2), vertices(1)) cross new Vector(vertices(1), vertices(0))) normalize(),
           - (((new Vector(vertices(2), vertices(1)) cross new Vector(vertices(1), vertices(0))) normalize())
            dot vertices.head.asVector()),
            diffusivity, reflectivity, color, vertices, translationOffset
        )


    override def closestIntersection(line: Line) : IntersectionData = {
        if ((line.vector dot normal) >= 0) null
        else {
            val testLine = new Line(line.point - translationOffset, line.vector)
            val closestIntersection = super.closestIntersection(testLine)
            if (closestIntersection == null) null
            else {
                if (insidePlane(closestIntersection.intersectionPoint, vertices, prevPositiveSign = false) /*|| insidePlane(closestIntersection.intersectionPoint, vertices, prevPositiveSign = true)*/) new IntersectionData(
                    closestIntersection.distance,
                    closestIntersection.intersectionPoint + translationOffset,
                    closestIntersection.normal,
                    closestIntersection.shape
                )
                else null
            }
        }
    }

    private def insidePlane(point: Point, vertices: List[Point], prevPositiveSign: Boolean) : Boolean = vertices match {
        case Nil      => true
        case v :: Nil => true
        case v1 :: v2 :: vs => {
            val lineToPoint = new Vector(point, v1)
            val edge = new Vector(v1, v2)

            val positiveSign = ((lineToPoint cross edge) dot normal) > 0

            if (positiveSign == prevPositiveSign) insidePlane(point, v2 :: vs, prevPositiveSign)
            else false
        }
    }
}