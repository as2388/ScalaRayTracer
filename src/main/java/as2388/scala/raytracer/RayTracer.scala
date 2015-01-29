package as2388.scala.raytracer

import scala.util.Random
import scalafx.scene.image.PixelWriter
import scalafx.scene.paint.Color

class RayTracer(val size: Size) {
    val TAU = 2 * Math.PI

    val shapes: List[Shape] =
        new Sphere(new Point(22, 5, 0), 3, 0.9, 0.1, Color.rgb(233, 30, 99)) :: //Pink 500
                new Sphere(new Point(21, 9, -2), 1, 0.5, 0.5, Color.rgb(103, 58, 183)) :: //Deep Purple 500
                new Sphere(new Point(36, -8, -2), 1, 0.5, 0.5, Color.rgb(103, 58, 183)) :: //Deep Purple 500
                new Sphere(new Point(15, 18, -2), 1, 0.5, 0.5, Color.rgb(103, 58, 183)) :: //Deep Purple 500
                new Sphere(new Point(10, -12, -2), 1, 0.5, 0.5, Color.rgb(103, 58, 183)) :: //Deep Purple 500
    //
                new Sphere(new Point(40, 12, -2), 1, 0.5, 0.5, Color.rgb(103, 58, 183)) :: //Deep Purple 500
                new Sphere(new Point(3, -7, -2), 1, 0.5, 0.5, Color.rgb(103, 58, 183)) :: //Deep Purple 500
                new Sphere(new Point(14, 0, -2), 1, 0.5, 0.5, Color.rgb(103, 58, 183)) :: //Deep Purple 500
                new Sphere(new Point(80, 19, -2), 1, 0.5, 0.5, Color.rgb(103, 58, 183)) :: //Deep Purple
                new Cuboid(
                    center = new Point(8, -10, -0.5),
                    XScale = 8, YScale = 2, ZScale = 5,
                    XRot = 0, YRot = 0, ZRot = TAU / 5,
                    diffusivity = 0.5, reflectivity = 0.5,
                    color = Color.rgb(76, 175, 80)
                ) ::
                new Cuboid(
                    center = new Point(58, 25, -1.5),
                    XScale = 3, YScale = 3, ZScale = 3,
                    XRot = 0, YRot = 0, ZRot = -TAU / 5,
                    diffusivity = 0.35, reflectivity = 0.75,
                    color = Color.rgb(0, 150, 136)
                ) ::
//                new Sphere(new Point(28, 15, -2), 1, 0.5, 0.5, Color.rgb(103, 58, 183)) :: //Deep Purple 500
//                new Sphere(new Point(28, 15, -0.25), 0.75, 0.5, 0.5, Color.rgb(103, 58, 183)) :: //Deep Purple 500
//                new Sphere(new Point(28, 15, 1), 0.5, 0.5, 0.5, Color.rgb(103, 58, 183)) :: //Deep Purple 500
//                new Sphere(new Point(28, 15, 1.75), 0.25, 0.5, 0.5, Color.rgb(103, 58, 183)) :: //Deep Purple 500
                new CheckeredPlane(new Vector(0, 0, 1), 3, 0.8, 0.2, Color.rgb(238, 238, 238), Color.rgb(158, 158, 158)) ::
//                                new CheckeredPlane(new Vector(0, 0, 1), -200, 1, 0, Color.rgb(238, 238, 238), Color.rgb(158, 158, 158)) ::
                Nil

    val randomizer = new Random()
//    val shapes: List[Shape] =
//        new CheckeredPlane(new Vector(0, 0, 1), 3, 0.8, 0.2, Color.rgb(238, 238, 238), Color.rgb(158, 158, 158)) ::
//                ((0 to 50).toList map (x=> new Cuboid(
//            center = new Point(randomizer.nextDouble() * 70 - 20, randomizer.nextDouble() * 50, randomizer.nextDouble() * 20),
//            XScale = 1, YScale = 1, ZScale = 1,
//            XRot = randomizer.nextDouble() * TAU, YRot = randomizer.nextDouble() * TAU, ZRot = randomizer.nextDouble() * TAU,
//            diffusivity = 0.5, reflectivity = 0.5,
//            color = Color.rgb(76, 175, 80)
//        )))

    val lights: List[PointLight] =
                new PointLight(new Point(-10, 50, 80), 0.60) ::
                new PointLight(new Point(-20, -50, 50), 0.30) ::
                Nil

    val camera = new Camera(
        screenCentre = new Point(22, 5, 0), //(22, ...
        distanceFromScreen = 70,
        screenPixelDimensions = size,
        pointsPerPixel = 0.00375,
        yaw = 0,
        pitch = TAU / 16,
        roll = 0
    )

    //    ----------- CIRCLE RING --------------------
//        val shapes: List[Shape] = new CheckeredPlane(new Vector(0, 0, 1), 3, 0.8, 0.2, Color.rgb(238, 238, 238), Color.rgb(158, 158, 158)) ::
//                buildCircle(new Point(22, 5, -2), 8, 2 * Math.PI / 9, 2 * Math.PI)
//
//
//        def buildCircle(centre: Point, radius: Double, angleSize: Double, currentAngle: Double): List[Shape] =
//            if (currentAngle <= 0) Nil
//            else new Sphere(new Point(radius * Math.sin(currentAngle) + centre.x, radius * Math.cos(currentAngle) + centre.y, centre.z),
//                1.2, 0.6, 0.4, Color.rgb(103, 58, 183)) ::
//                    buildCircle(centre, radius, angleSize, currentAngle - angleSize)
//
//        val lights: List[PointLight] = new PointLight(new Point(22, 5, 5), 0.9) ::
//                Nil
//
//        val camera = new Camera(
//            viewPoint = new Point(-20, 5, 20),
//            viewDirection = new Vector(0.65, 0, -0.35).normalize(),
//            viewUp = new Vector(0.35, 0, 0.65).normalize(),
//            distanceFromScreen = 15,
//            screenPixelDimensions = new Size(2560, 1440),
//            pointsPerPixel = 0.006
//        )
    //    ---------------------------------------

    def closestShape(line: Line): IntersectionData =
        (shapes map (_ closestIntersection line)).
                foldLeft(null: IntersectionData)((b, a) =>
            if (b == null && a != null && a.distance > 0) a
            else
            if (a != null && b != null && a.distance > 0 && a.distance < b.distance) a
            else b)

    def diffuseIntensity(intersectionData: IntersectionData) =
        lights.map(light =>
            if (inShadow(shapes, new Line(
                intersectionData.intersectionPoint,
                new Vector(light.location, intersectionData.intersectionPoint)), intersectionData.shape)
            ) 0
            else (intersectionData.normal dot (new Vector(light.location, intersectionData.intersectionPoint) normalize()))
                    * light.intensity
        ).filter(_ > 0).sum

    /**
     * Returns true if the line from an intersection point intersects another shape; false otherwise
     * @param shapes            List of shapes to check against
     * @param line              Line
     * @param ignoreShape       Shape to exclude from check (should probably be shape we're checking if is in shadow
     * @return
     */
    def inShadow(shapes: List[Shape], line: Line, ignoreShape: Shape) =
        ((shapes filter (_ != ignoreShape)) map (_ closestIntersection line) count (x => x != null && x.distance > 0)) != 0

    def colorRay(line: Line, impact: Double): Color = {
        val closestIntersection: IntersectionData = closestShape(line)

        if (closestIntersection == null || impact < 0.05) Color.rgb(0, 0, 0)
        else {
            val diffuseIllumination = diffuseIntensity(closestIntersection)
            val ambientIllumination = 0.1
            val diffuseAmbientColor = colorMultiply(closestIntersection.shape.color(closestIntersection.intersectionPoint),
                (diffuseIllumination + ambientIllumination) * closestIntersection.shape.diffusivity)

            if (closestIntersection.shape.reflectivity == 0) diffuseAmbientColor
            else {
                val reflectedVector: Vector = line.vector subtract (closestIntersection.normal scalarMultiply (line.vector dot closestIntersection.normal) * 2) //don't need to normalize
                val reflectedRay = new Line(closestIntersection.intersectionPoint, reflectedVector)
                val specularColor = colorRay(reflectedRay, impact * closestIntersection.shape.reflectivity)

                val reflectivity = closestIntersection.shape.reflectivity
                Color.rgb(
                    ((diffuseAmbientColor.red + specularColor.red * reflectivity) * 255).toInt,
                    ((diffuseAmbientColor.green + specularColor.green * reflectivity) * 255).toInt,
                    ((diffuseAmbientColor.blue + specularColor.blue * reflectivity) * 255).toInt
                )
            }
        }
    }

    def colorPixel(pixelPoint: PixelPoint) = {
        val lineToPixel = camera lineToPixel pixelPoint
        colorRay(lineToPixel, 1.0)
    }

    def focus(pixelPoint: PixelPoint) = {
        val sampledSubPixels = for {
            yawChange <- -TAU / 400 to TAU / 400 by TAU / 4000
            pitchChange <- -TAU / 400 to TAU / 400 by TAU / 4000
        } yield {
            colorRay(camera lineToPixel(pixelPoint, yawChange, pitchChange), 1.0)
        }

        averageColors(sampledSubPixels.toList)
    }

    def antiAlias(pixelPoint: PixelPoint) = {
        val sampledSubPixels = for {
            x <- -2 to 2
            y <- -2 to 2
        } yield {
            colorPixel(new PixelPoint(pixelPoint.x + x.toDouble / 4, pixelPoint.y + y.toDouble / 4))
        }

        averageColors(sampledSubPixels.toList)
    }

    def adaptiveAntiAlias(pixelPoint: PixelPoint) = {
        if (pixelPoint.x < 0.35 * size.width || pixelPoint.y > 0.65 * size.width ||
            pixelPoint.y < 0.35 * size.height || pixelPoint.y > 0.65 * size.height) focus(pixelPoint)
        else antiAlias(pixelPoint)
    }

    /**
     * Given a list of colors, returns a new color with the list's (r,g,b) values averaged
     * @param colors    List of colors to average
     * @return Average color of list
     */
    def averageColors(colors: List[Color]) =
        Color.rgb(
            ((colors map (_.red)).sum / colors.length * 255).toInt,
            ((colors map (_.green)).sum / colors.length * 255).toInt,
            ((colors map (_.blue)).sum / colors.length * 255).toInt
        )

    /**
     * Multiplies each field of a color by a given intensity
     * @param color         Color to modify
     * @param intensity     Intensity to multiply by
     * @return
     */
    def colorMultiply(color: Color, intensity: Double) =
        Color.rgb((color.red * 255 * intensity).toInt, (color.green * 255 * intensity).toInt, (color.blue * 255 * intensity).toInt)

    def writeToImage(writer: PixelWriter) = {
        var remaining = size.width
        (0 to size.width - 1).par foreach (x => {
            (0 to size.height - 1).par foreach (y => writer setColor(x, y, antiAlias(new PixelPoint(x, y))))
            remaining -= 1
            if (remaining % 20 == 0)
                println((((size.width - remaining).toDouble / size.width.toDouble) * 10000).floor / 100 + "% done (" + remaining + " columns remain)")
        })
    }
}