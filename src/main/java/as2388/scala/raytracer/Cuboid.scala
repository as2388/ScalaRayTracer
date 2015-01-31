package as2388.scala.raytracer

import scalafx.scene.paint.Color

class Cuboid(val center: Point, val XScale: Double, val YScale: Double, val ZScale: Double,
             val XRot: Double, val YRot: Double, val ZRot: Double,
             val diffusivity: Double, val reflectivity: Double, val color: Color, val mass: Double)
        extends Shape with MinMax {

    val halfXScale = XScale / 2
    val halfYScale = YScale / 2
    val halfZScale = ZScale / 2

    val boundingSphere = new Sphere(center, (XScale::YScale::ZScale::Nil).max, 0, 0, null, mass)

    val planes = ( //X front
                new FinitePlane(new Vector(-1, 0, 0), -halfXScale, diffusivity, reflectivity, color,
                        new Point(-halfXScale, halfYScale, halfZScale) :: new Point(-halfXScale, halfYScale, -halfZScale) ::
                        new Point(-halfXScale, -halfYScale, -halfZScale) :: new Point(-halfXScale, -halfYScale, halfZScale) ::
                        new Point(-halfXScale, halfYScale, halfZScale) :: Nil, mass
                ) :: //Y right
                new FinitePlane(new Vector(0, -1, 0), -halfYScale, diffusivity, reflectivity, color,
                        new Point(-halfXScale, -halfYScale, -halfZScale) :: new Point(-halfXScale, -halfYScale, halfZScale) ::
                        new Point(halfXScale, -halfYScale, halfZScale) :: new Point(halfXScale, -halfYScale, -halfZScale) ::
                        new Point(-halfXScale, -halfYScale, -halfZScale) :: Nil, mass
                ) :: //Z top
                new FinitePlane(new Vector(0, 0, 1), -halfZScale, diffusivity, reflectivity, color,
                        new Point(-halfXScale, -halfYScale, halfZScale) :: new Point(-halfXScale, halfYScale, halfZScale) ::
                        new Point(halfXScale, halfYScale, halfZScale) :: new Point(halfXScale, -halfYScale, halfZScale) ::
                        new Point(-halfXScale, -halfYScale, halfZScale) :: Nil, mass
                ) :: //X back
                new FinitePlane(new Vector(1, 0, 0), -halfXScale, diffusivity, reflectivity, color,
                        new Point(halfXScale, halfYScale, halfZScale) :: new Point(halfXScale, halfYScale, -halfZScale) ::
                        new Point(halfXScale, -halfYScale, -halfZScale) :: new Point(halfXScale, -halfYScale, halfZScale) ::
                        new Point(halfXScale, halfYScale, halfZScale) :: Nil, mass
                ) :: //Y left
                new FinitePlane(new Vector(0, 1, 0), -halfYScale, diffusivity, reflectivity, color,
                    new Point(-halfXScale, halfYScale, -halfZScale) :: new Point(-halfXScale, halfYScale, halfZScale) ::
                    new Point(halfXScale, halfYScale, halfZScale) :: new Point(halfXScale, halfYScale, -halfZScale) ::
                    new Point(-halfXScale, halfYScale, -halfZScale) :: Nil, mass
                ) :: //Z bottom
                new FinitePlane(new Vector(0, 0, -1), -halfZScale, diffusivity, reflectivity, color,
                    new Point(-halfXScale, -halfYScale, -halfZScale) :: new Point(-halfXScale, halfYScale, -halfZScale) ::
                    new Point(halfXScale, halfYScale, -halfZScale) :: new Point(halfXScale, -halfYScale, -halfZScale) ::
                    new Point(-halfXScale, -halfYScale, -halfZScale) :: Nil, mass
                ) ::
                Nil
            ) map (plane => new FinitePlane(plane.normal rotateX XRot rotateY YRot rotateZ ZRot, plane.distance,
                    plane.diffusivity, plane.reflectivity, plane.color,
                    rotate(plane.vertices), mass, center))

    private def rotate(vertices: List[Point]) = vertices map (_ rotateX XRot rotateY YRot rotateZ ZRot)

    override def color(point: Point): Color = color

    override def closestIntersection(line: Line): IntersectionData = {
        if ((boundingSphere closestIntersection line) == null) null
        else {
            val nonNullIntersections = (planes map (_ closestIntersection line)) filter (_ != null)
            if (nonNullIntersections == List.empty) null
            else nonNullIntersections.foldLeft(nonNullIntersections.head)((b: IntersectionData, a: IntersectionData) => if (a.distance < b.distance) a else b)
        }
    }
}


