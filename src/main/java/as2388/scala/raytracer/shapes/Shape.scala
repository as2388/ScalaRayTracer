package as2388.scala.raytracer.shapes

import as2388.scala.raytracer.{Color, IntersectionData, Line, Point}

trait Shape {
    /**
     * @return Proportion of illumination due to diffuse lighting.
     */
    def diffusivity: Double

    /**
     * @return Proportion of illumination due to reflective lighting.
     */
    def reflectivity: Double

    /**
     * Computes the ambient color of the shape at a given point on the shape.
     * @param point Point on the shape to get ambient color of.
     * @return      Ambient color of the shape at the given point.
     */
    def color(point: Point): Color

    /**
     * Computes the intersection data for the closest intersection between this shape and the given line.
     * @param line Line to compute intersection with
     * @return     Intersection data for closest intersection with line if there is an intersection,
     *             otherwise returns null
     */
    def closestIntersection(line: Line): IntersectionData
}
