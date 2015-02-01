package as2388.scala.raytracer

import scalafx.scene.image.PixelWriter
import scalafx.scene.paint.Color

class RayTracer(val configuration: Configuration) {
    val TAU = 2 * Math.PI

//    val randomizer = new Random()
//    val shapes: List[Shape] =
//        new CheckeredPlane(new Vector(0, 0, 1), 3, 0.8, 0.2, Color.rgb(238, 238, 238), Color.rgb(158, 158, 158)) ::
//                ((0 to 50).toList map (x=> new Cuboid(
//            center = new Point(randomizer.nextDouble() * 70 - 20, randomizer.nextDouble() * 50, randomizer.nextDouble() * 20),
//            XScale = 1, YScale = 1, ZScale = 1,
//            XRot = randomizer.nextDouble() * TAU, YRot = randomizer.nextDouble() * TAU, ZRot = randomizer.nextDouble() * TAU,
//            diffusivity = 0.5, reflectivity = 0.5,
//            color = Color.rgb(76, 175, 80)
//        )))

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

    val size = configuration.imageSize
    val shapes = configuration.shapes
    val lights = configuration.lights
    val camera = configuration.camera
    val singularities = configuration.singularities
    val singularityDepthLimit = configuration.singularityDepthLimit

    def closestShape(line: Line, distanceSoFar: Double): IntersectionData = {
        lazy val closest = (shapes map (_ closestIntersection line)).foldLeft(null: IntersectionData)((b, a) =>
            if (b == null && a != null && a.distance > 0) a
            else
            if (a != null && b != null && a.distance > 0 && a.distance < b.distance) a
            else b)

        singularities match {
            case Nil => closest
            case _ =>
                if (distanceSoFar > singularityDepthLimit) null
                else if (closest == null || closest.distance > 1) {
                    val endPoint = line.point add line.vector.scalarMultiply(1).asPoint()

                    val gravitationalForces: List[Vector] = singularities map (singularity => new Vector(endPoint, singularity.location)
                            scalarMultiply (singularity.strength / Math.pow(endPoint distanceTo singularity.location, 1)))

                    val newVector = gravitationalForces.foldLeft(line.vector)((b: Vector, a: Vector) => a add b) normalize()

                    closestShape(new Line(endPoint, newVector), distanceSoFar + 1)
                }
                else closest
        }
    }

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
        val closestIntersection: IntersectionData = closestShape(line, 0)

        if (closestIntersection == null || impact < 0.05) Color.rgb(0, 0, 0) //Color.rgb(38, 50, 56)
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
            (0 to size.height - 1) foreach (y => writer setColor(x, y, colorPixel(new PixelPoint(x, y))))
            remaining -= 1
            if (remaining % 20 == 0)
                println((((size.width - remaining).toDouble / size.width.toDouble) * 10000).floor / 100 + "% done (" + remaining + " columns remain)")
        })
    }
}