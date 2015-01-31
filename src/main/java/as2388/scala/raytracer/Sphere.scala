package as2388.scala.raytracer

import scalafx.scene.paint.Color

class Sphere(val center: Point, val radius: Double, val diffusivity: Double, val reflectivity: Double, color: Color, val mass: Double)
        extends Shape with MinMax {

    override def color(point: Point): Color = color

    /**
     * Finds the nearest intersection point between the line X = P + sT
     * and this sphere, which has equation r^2 = (X - C)^2
     * @param line  Line to test for closest intersection with
     * @return      Distance from line.point to closest intersection
     */
    override def closestIntersection(line: Line): IntersectionData = {
        // Substituting the equation of the line into the equation of the sphere gives
        // the equation:
        // s^2 T^2 + 2 s T^2 (P - C) + (P - C)^2 - r^2
        // Solve for s to get distance to closest intersection
        val b1 = line.vector scalarMultiply 2                  // 2 * T
        val PMinusC = (line.point subtract center) asVector()  // P - C

        val a = line.vector dot line.vector                    // a = T^2
        val b = b1 dot PMinusC                                 // b = 2T (P - C)
        val c = (PMinusC dot PMinusC) - radius * radius        // c = (P - C)^2 - r^2

        val discriminant = b * b - 4 * a * c

        if (discriminant <= 0) null
        else {
            val d = Math sqrt discriminant

            val intersectionDistance1 = (-b + d) / (2 * a)
            val intersectionDistance2 = (-b - d) / (2 * a)

            val closestIntersectionDistance = min(intersectionDistance1, intersectionDistance2)
            val closestIntersectionPoint = line.point addVector (line.vector scalarMultiply closestIntersectionDistance)
            val intersectionNormal = new Vector(closestIntersectionPoint, center) normalize()

            new IntersectionData(closestIntersectionDistance, closestIntersectionPoint, intersectionNormal, this)
        }
    }
}
