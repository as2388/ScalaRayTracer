package as2388.scala.raytracer.shapes

import as2388.scala.raytracer.{IntersectionData, Line, Point}

import scalafx.scene.paint.Color

trait Shape {
    def diffusivity: Double

    def reflectivity: Double

    def color(point: Point): Color

    def closestIntersection(line: Line): IntersectionData
}