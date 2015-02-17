package as2388.scala.raytracer

import java.awt.image.BufferedImage

import as2388.scala.raytracer.shapes.Shape

import scalafx.scene.image.PixelWriter

class RayTracer(val configuration: Configuration) {
    def random = Math.random()

    val size = configuration.imageSize
    val shapes = configuration.shapes
    val lights = configuration.lights
    val ambientIntensity = configuration.ambientIntensity
    val camera = configuration.camera
    val singularities = configuration.singularities
    val singularityDepthLimit = configuration.singularityDepthLimit
    val enableShadows = configuration.enableShadows
    val enableDiffuse = configuration.enableDiffuse
    val enableReflections = configuration.enableReflections
    val antiAliasingMode = configuration.antiAliasingMode
    val focusMode = configuration.focusMode

    /**
     * Produces intersection data for the closest shape to intersect a line. If no intersection is found, returns null.
     * If singularities are enabled, recursively simulates space-time warping.
     * @param line          The line to find the closest intersection with
     * @param distanceSoFar If using singularities, the distance travelled so far. If this reaches the
     *                      singularityDepthLimit, recursion is terminated.
     * @return              IntersectionData for closest intersecting shape if there is one, else null
     */
    private def closestShape(line: Line, distanceSoFar: Double): IntersectionData = {
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
                    val endPoint = line.point + (line.vector * 1)

                    val gravitationalForces: List[Vector] = singularities map (singularity =>
                        new Vector(endPoint, singularity.location) *
                            (singularity.strength / Math.pow(endPoint distanceTo singularity.location, 1)))

                    val newVector = gravitationalForces.foldLeft(line.vector)((b: Vector, a: Vector) => a + b) normalize()

                    closestShape(new Line(endPoint, newVector), distanceSoFar + 1)
                }
                else closest
        }
    }

    /**
     * Computes the color at an intersection point due due to diffuse lighting
     * @param intersectionData Describes intersection point. The point, normal, and shape are needed.
     * @return                 Color at an intersection point due to diffuse lighting
     */
    private def diffuseColor(intersectionData: IntersectionData): Color =
        /* For each light, if there is in object obstructing the straight line to the light source (if inShadow)
           then the contribution from that light is the colour black.
           Otherwise, provided the normal is of the intersection is pointing toward the viewer, the colour contributed
            is that from the diffuse equation.
           Having computed the colour contribution from each light source, sum the colours to get the combined colour
            from all diffuse lights */
        lights.map(light =>
            if (inShadow(shapes, new Line(
                intersectionData.intersectionPoint,
                new Vector(light.location, intersectionData.intersectionPoint)), intersectionData.shape)
            ) new Color(0, 0, 0)
            else {
                val normal = intersectionData.normal dot (new Vector(
                    light.location, intersectionData.intersectionPoint) normalize())

                if (normal > 0) light.color * (normal * light.intensity)
                else new Color(0, 0, 0)
            }
        ).foldLeft(new Color(0, 0, 0))((b, a) => a + b)

    /**
     * Returns true if the line from an intersection point intersects another shape; false otherwise
     * @param shapes            List of shapes to check against
     * @param line              Line
     * @param ignoreShape       Shape to exclude from check (should probably be shape we're checking if is in shadow
     * @return
     */
    private def inShadow(shapes: List[Shape], line: Line, ignoreShape: Shape) =
        enableShadows match {
            case true => ((shapes filter (_ != ignoreShape)) map (_ closestIntersection line) count (x => x != null && x.distance > 0)) != 0
            case _ => false
        }

    /**
     * Determines the color of a ray of light from an object.
     * @param line   Line pointing in the reverse direction of the emitted ray (i.e. towards an object)
     * @param impact The proportion of the colour of the ray entering the camera is made up of this ray.
     *               Used for terminating the function
     * @return       Color of a ray of light from an object
     */
    private def colorRay(line: Line, impact: Double): Color = {
        val closestIntersection: IntersectionData = closestShape(line, 0)

        // If the impact is less than 5 % then the contribution made by this ray is unnoticeable, so terminate
        if (closestIntersection == null || impact < 0.05) new Color(0, 0, 0)
        else {
            // Compute the contributions to the intersecting object's color made by diffuse and ambient lighting equations
            val diffuse: Color = enableDiffuse match {
                case true => diffuseColor (closestIntersection) * closestIntersection.shape.diffusivity *
                    closestIntersection.shape.color (closestIntersection.intersectionPoint)
                case _    => new Color(0, 0, 0)
            }
            val ambient: Color = closestIntersection.shape.color(closestIntersection.intersectionPoint) * ambientIntensity
            val diffuseAmbientColor = diffuse + ambient

            if (closestIntersection.shape.reflectivity == 0 || !enableReflections) diffuseAmbientColor
            else {
                // if the shape is reflected, compute the direction of the reflected ray and recursively call this
                // function to find out the colour of the reflected ray.
                val reflectedVector: Vector = line.vector - (closestIntersection.normal *
                        (line.vector dot closestIntersection.normal) * 2) //don't need to normalize
                val reflectedRay = new Line(closestIntersection.intersectionPoint, reflectedVector)
                val specularColor = colorRay(reflectedRay, impact * closestIntersection.shape.reflectivity)

                val reflectivity = closestIntersection.shape.reflectivity

                // Finally, sum the colors contributed by each lighting equation.
                diffuseAmbientColor + (specularColor * reflectivity)
            }
        }
    }

    /**
     * Determines the color of a pixel on the output image
     * @param pixelPoint    The point on the output image to compute the colour of
     * @param yawChange     Variation in the camera's yaw to make (used for depth of field)
     * @param pitchChange   Variation in the camera's pitch to make (used for depth of field)
     * @return  The color of the pixel
     */
    private def colorPixel(pixelPoint: PixelPoint, yawChange: Double, pitchChange: Double) = {
        val lineToPixel = camera lineToPixel (pixelPoint, yawChange, pitchChange)
        colorRay(lineToPixel, 1.0)
    }

    /**
     * Supersamples the image with the camera at slightly different angles to simulate depth of field.
     * @param pixelPoint The point on the output image to compute the color with depth of field enabled of.
     * @param count      Number of supersamples to take in each variation direction, so that the number of.
     *                   supersamples taken is count squared.
     * @param angle      The maximum angle to peturb the yaw and pitch by.
     * @return           Color of a pixel on the output image with supersampled depth of field.
     */
    private def focus(pixelPoint: PixelPoint, count: Int, angle: Double) = {
        val sampledSubPixels = for {
            yawChange <- -angle to angle by angle * 2 / count
            pitchChange <- -angle to angle by angle * 2 / count
        } yield {
            // Perform jittered supersampling for better results (prevents banding)
            val randomYaw = random * angle * 2 / count - angle / count
            val randomPitch = random * angle * 2 / count - angle / count

            if (pixelPoint.x < 0.38 * size.width || pixelPoint.y > 0.62 * size.width ||
                    pixelPoint.y < 0.38 * size.height || pixelPoint.y > 0.62 * size.height)
                colorPixel(pixelPoint, yawChange + randomYaw, pitchChange + randomPitch)
            else antiAliasingFunction(pixelPoint, yawChange + randomYaw, pitchChange + randomPitch)
        }

        averageColors(sampledSubPixels.toList)
    }

    /**
     * Perform regular antialiasing to determine the color of a pixel
     * @param pixelPoint  The point on the output image to compute the anti-aliased color of.
     * @param count       Number of samples to take in each of directions x and y
     * @param yawChange   Variation in yaw to camera's default (used for focus).
     * @param pitchChange Variation in pitch to camera's default (used for focus).
     * @return            Anti-aliased colour of a pixel on the output image.
     */
    private def antiAlias(pixelPoint: PixelPoint, count: Int, yawChange: Double, pitchChange: Double) = {
        val sampledSubPixels = for {
            x <- -0.50 to 0.50 by 1.0 / count
            y <- -0.50 to 0.50 by 1.0 / count
        } yield {
            // Cheaper to take more samples than to Jitter
            val xRandom = random * 1.0 / count - 0.5 / count
            val yRandom = random * 1.0 / count - 0.5 / count

            colorPixel(new PixelPoint(pixelPoint.x + x + xRandom, pixelPoint.y + y + yRandom), yawChange, pitchChange)
        }

        averageColors(sampledSubPixels.toList)
    }


    /**
     * Given a list of colors, returns a new color with the list's (r,g,b) values averaged
     * @param colors    List of colors to average
     * @return          Average color of the list of colors
     */
    private def averageColors(colors: List[Color]) =
        new Color(
            colors.map(_.r).sum / colors.length,
            colors.map(_.g).sum / colors.length,
            colors.map(_.b).sum / colors.length
        )

    private def antiAliasingFunction(pixelPoint: PixelPoint, yawChange: Double = 0, pitchChange: Double = 0) = antiAliasingMode match {
        case AntiAliasingRegular(count) => antiAlias(pixelPoint, count, yawChange, pitchChange)
        case AntiAliasingRegularCenter(count, xmin, ymin) => if (pixelPoint.x < xmin || pixelPoint.x > size.width - xmin ||
                pixelPoint.y < ymin || pixelPoint.y > size.height - ymin ) colorPixel(pixelPoint, yawChange, pitchChange)
            else antiAlias(pixelPoint, count, yawChange, pitchChange)
        case AntiAliasingNone()         => colorPixel(pixelPoint, yawChange, pitchChange)
    }

    private def focusFunction(pixelPoint: PixelPoint) = focusMode match {
        case FocusSome(count, angle)    => focus(pixelPoint, count, angle)
        case FocusNone()                => antiAliasingFunction(pixelPoint)
    }

    /**
     * Performs the RayTrace operation, writing results to a buffered image.
     * Everytime a column is completed, prints the number of remaining columns to the console
     * @param writer BufferedImage to write output image to.
     */
    def writeToImage(writer: BufferedImage, startX: Int, startY: Int, width: Int, height: Int) = {
//        var remaining = size.width

        (startX to startX + width - 1).par foreach (x => {
            (startY to startY + height - 1).par foreach (y => {
                val color = focusFunction(new PixelPoint(x, y)).getSafeColor
                writer.setRGB(x, y, new java.awt.Color((color.r * 255).toInt, (color.g * 255).toInt, (color.b * 255).toInt).getRGB)
            })
        })

//        (0 to size.width - 1).par foreach (x => {
//            (0 to size.height - 1).par foreach (y => {
//                val color = focusFunction(new PixelPoint(x, y)).getSafeColor
//                writer.setRGB(x, y, new java.awt.Color((color.r * 255).toInt, (color.g * 255).toInt, (color.b * 255).toInt).getRGB)
//            })
//            remaining -= 1
//            if (remaining % 1 == 0)
//                println((((size.width - remaining).toDouble / size.width.toDouble) * 10000).floor / 100 +
//                        "% done (" + remaining + " columns remain)")
//        })
    }
    /**
     * Performs the RayTrace operation, writing results to a ScalaFX PixelWriter.
     * Everytime a column is completed, prints the number of remaining columns to the console
     * @param writer PixelWriter to write output image to.
     */
    def writeToImage(writer: PixelWriter) = {
        var remaining = size.width

        (0 to size.width - 1).par foreach (x => {
            (0 to size.height - 1) foreach (y => {
                val color = focusFunction(new PixelPoint(x, y))
                writer.setColor(x, y, color.toScalaFXColor)
            })
            remaining -= 1
            if (remaining % 1 == 0)
                println((((size.width - remaining).toDouble / size.width.toDouble) * 10000).floor / 100 +
                        "% done (" + remaining + " columns remain)")
        })
    }
}