package as2388.scala.raytracer.examples

import as2388.scala.raytracer._
import as2388.scala.raytracer.shapes._


abstract class ConfigurationManager {
    def getConfiguration: Configuration
}

/**
 * Renders a red sphere with a singularity at its centre, and a magnified purple sphere behind it
 * @param size  Image size
 */
class LenseConfiguration(val size: Size) extends ConfigurationManager {
    override def getConfiguration = {
        new Configuration(
            imageSize = size,

            shapes =
                new Sphere(new Point(28, 6, 0), 2, 1.0, 0, new Color(255, 193, 7)) :: Nil,

            lights =
                new PointLight(new Point(-10, 50, 80), 0.30) ::
                new PointLight(new Point(54, 50, 80), 0.30) ::
                new PointLight(new Point(-20, -50, 50), 0.2) ::
                new PointLight(new Point(28, 5, -70), 0.20) ::
                Nil,

            camera = new Camera(
                screenCentre = new Point(22, 5, 0),
                distanceFromScreen = 70,
                screenPixelDimensions = size,
                pointsPerPixel = .35,
                yaw = 0,
                pitch = 0,
                roll = 0
            ),

            singularities =
                new Singularity(new Point(22, 5, 0), -0.037) ::
                Nil,

            singularityDepthLimit = 300
        )
    }
}

/**
 * Renders the image on this project's github home page, a checkerboard with spheres and cuboids on it
 * @param size
 * @param iter
 */
class CheckerboardConfiguration(val size: Size, val iter: Double) extends ConfigurationManager {
    lazy val TAU = 2 * Math.PI

    val XRot = 0
    val YRot = 0
    val ZRot = iter
    private def rotate(vertices: List[Point]) = vertices map (_ rotateX XRot rotateY YRot rotateZ ZRot)

    override def getConfiguration = {
        val gr = (1 + Math.sqrt(5)) / 2

        val a = new Point(gr, 0, 1 / gr)
        val b = new Point(-gr, 0, 1 / gr)
        val c = new Point(-gr, 0, -1 / gr)
        val d = new Point(gr, 0, -1 / gr)

        val e = new Point(1 / gr, gr, 0)
        val f = new Point(1 / -gr, gr, 0)
        val g = new Point(-1 / -gr, gr, 0)
        val h = new Point(-1 / gr, gr, 0)

        val i = new Point(0, 1 / gr, gr)
        val j = new Point(0, 1 / gr, -gr)
        val k = new Point(0, -1 / gr, -gr)
        val l = new Point(0, -1 / gr, gr)

        val m = new Point(1, 1, 1)
        val n = new Point(1, -1, 1)
        val o = new Point(-1, -1, 1)
        val p = new Point(-1, 1, 1)
        val q = new Point(-1, 1, -1)
        val r = new Point(1, 1, -1)
        val s = new Point(1, -1, -1)
        val t = new Point(-1, -1, -1)

        val planes = (new FinitePlane(
            new Point(-1, 0, 0) ::
                    new Point(0, -1, 0) ::
                    new Point(0, 0, 1) ::
                    new Point(-1, 0, 0) ::
                    Nil,
            0.90, 0.10, ColorUtils.fromHex("FF5722"), new Point(22, 5, 0)
        ) ::
                new FinitePlane(
                    new Point(0, -1, 0) ::
                            new Point(1, 0, 0) ::
                            new Point(0, 0, 1) ::
                            new Point(0, -1, 0) ::
                            Nil,
                    0.90, 0.10, ColorUtils.fromHex("FF5722"), new Point(22, 5, 0)
                ) ::
                new FinitePlane(
                    new Point(1, 0, 0) ::
                            new Point(0, 1, 0) ::
                            new Point(0, 0, 1) ::
                            new Point(1, 0, 0) ::
                            Nil,
                    0.90, 0.10, ColorUtils.fromHex("FF5722"), new Point(22, 5, 0)
                ) ::
                new FinitePlane(
                    new Point(0, 0, 1) ::
                            new Point(0, 1, 0) ::
                            new Point(-1, 0, 0) ::
                            new Point(0, 0, 1) ::
                            Nil,
                    0.90, 0.10, ColorUtils.fromHex("FF5722"), new Point(22, 5, 0)
                ) ::

                new FinitePlane(
                    new Point(-1, 0, 0) ::
                            new Point(0, 0, -1) ::
                            new Point(0, -1, 0) ::
                            new Point(-1, 0, 0) ::
                            Nil,
                    0.90, 0.10, ColorUtils.fromHex("FF5722"), new Point(22, 5, 0)
                ) ::
                new FinitePlane(
                    new Point(0, -1, 0) ::
                            new Point(0, 0, -1) ::
                            new Point(1, 0, 0) ::
                            new Point(0, -1, 0) ::
                            Nil,
                    0.90, 0.10, ColorUtils.fromHex("FF5722"), new Point(22, 5, 0)
                ) ::

                new FinitePlane(
                    new Point(0, 1, 0) ::
                            new Point(1, 0, 0) ::
                            new Point(0, 0, -1) ::
                            new Point(0, 1, 0) ::
                            Nil,
                    0.90, 0.10, ColorUtils.fromHex("FF5722"), new Point(22, 5, 0)
                ) ::
                new FinitePlane(
                    new Point(-1, 0, 0) ::
                            new Point(0, 1, 0) ::
                            new Point(0, 0, -1) ::
                            new Point(-1, 0, 0) ::
                            Nil,
                    0.90, 0.10, ColorUtils.fromHex("FF5722"), new Point(22, 5, 0)
                ) :: Nil) map (plane => new FinitePlane(plane.normal rotateX XRot rotateY YRot rotateZ ZRot, plane.distance,
            plane.diffusivity, plane.reflectivity, plane.color,
            rotate(plane.vertices), new Point(22,5,0)))


        new Configuration(
            imageSize = size,
//                    new FinitePlane(
//                        new Point(-1, 1, -1) ::
//                        new Point(-1 / gr, gr, 0) ::
//                        new Point(-1, 1, 1) ::
//                        new Point(-gr, 0, 1 / gr) ::
//                        new Point(-gr, 0, -1 / gr) ::
//                        new Point(-1, 1, -1) ::
//                        Nil,
//                        0.90, 0.10, ColorUtils.fromHex("FF5722"), new Point(22, 5, 0)
//                    ) ::
//                    new FinitePlane(
//                        new Point(-gr, 0, -1 / gr) ::
//                        new Point(-gr, 0, 1 / gr) ::
//                        new Point(-1 , -1 , 1) ::
//                        new Point(-1 / gr, -gr, 0) ::
//                        new Point(-1, -1, -1) ::
//                        new Point(-gr, 0, -1 / gr) ::
//                        Nil,
//                        0.90, 0.10, ColorUtils.fromHex("FF5722"), new Point(22, 5, 0)
//                    ) ::
//                    new FinitePlane(
//                        new Point(-gr, 0, -1 / gr) ::
//                        new Point(-1, -1, -1) ::
//                        new Point(0, -1 / gr, -gr) ::
//                        new Point(0, 1 / gr, -gr) ::
//                        new Point(-1, 1, -1) ::
//                        new Point(-gr, 0, -1 / gr) ::
//                        Nil,
//                        0.90, 0.10, ColorUtils.fromHex("FF5722"), new Point(22, 5, 0)
//                    ) ::
//                    new FinitePlane(
//                        new Point(-gr, 0, 1 / gr) ::
//                        new Point(-1, 1, 1) ::
//                        new Point(0, 1 / gr, gr) ::
//                        new Point(0, -1 / gr, gr) ::
//                        new Point(-1, -1, 1) ::
//                        new Point(-gr, 0, 1 / gr) ::
//                        Nil,
//                        0.90, 0.10, ColorUtils.fromHex("FF5722"), new Point(22, 5, 0)
//                    ) ::
//                    new FinitePlane(
//                        new Point(-1, 1, 1) ::
//                        new Point(- 1/ gr, gr, 0) ::
//                        new Point(1 / gr, gr, 0) ::
//                        new Point(1, 1, 1) ::
//                        new Point(0, 1/ gr, gr) ::
//                        new Point(-1, 1, 1) ::
//                        Nil,
//                        0.90, 0.10, ColorUtils.fromHex("FF5722"), new Point(22, 5, 0)
//                    ) ::
//                    new FinitePlane(
//                        new Point(-1, 1, 1) ::
//                        new Point(0, -1 / gr, gr) ::
//                        new Point(1, -1, 1) ::
//                        new Point(1 / gr, -gr, 0) ::
//                        new Point(-1 / gr, -gr, 0) ::
//                        new Point(-1, 1, 1) ::
//                        Nil,
//                        0.90, 0.10, ColorUtils.fromHex("FF5722"), new Point(22, 5, 0)
//                    ) ::
//                    new FinitePlane(
//                        new Point(gr, 0, -1 / gr) ::
//                        new Point(1, 1, -1) ::
//                        new Point(0, 1 / gr, - gr) ::
//                        new Point(0, -1 / gr, -gr) ::
//                        new Point(1, -1, -1) ::
//                        new Point(gr, 0, -1 / gr) ::
//                        Nil,
//                        0.90, 0.10, ColorUtils.fromHex("FF5722"), new Point(22, 5, 0)
//                    ) ::
//                    new FinitePlane(
//                        new Point(1, -1, 1) ::
//                        new Point(gr, 0, 1 / gr) ::
//                        new Point(gr, 0, -1 / gr) ::
//                        new Point(1 / gr, -gr, 0) ::
//                        new Point(1, -1, 1) ::
//                        new Point(1, -1, 1) ::
//                        Nil,
//                        0.90, 0.10, ColorUtils.fromHex("FF5722"), new Point(22, 5, 0)
//                    ) ::
//                    new FinitePlane(
//                        new Point(1, -1, 1) ::
//                        new Point(gr, 0, 1 / gr) ::
//                        new Point(gr, 0, -1 / gr) ::
//                        new Point(1 / gr, -gr, 0) ::
//                        new Point(1, -1, 1) ::
//                        new Point(1, -1, 1) ::
//                        Nil,
//                        0.90, 0.10, ColorUtils.fromHex("FF5722"), new Point(22, 5, 0)
//                    ) ::

//                    new FinitePlane(
//                        new Point(0, -1 / gr, gr) ::
//                        new Point(0, 1 / gr, gr) ::
//                        new Point(1, 1, 1) ::
//                        new Point(gr, 0, 1/gr) ::
//                        new Point(1, -1, 1) ::
//                        new Point(0, -1 / gr, gr) ::
//                        Nil,
//                        0.90, 0.10, ColorUtils.fromHex("FF5722"), new Point(22, 5, 0)
//                    ) ::

//                    new FinitePlane(
//                        new Point(-0.5, 0.5, ) ::
//                        new Point(-1 / gr, gr, 0) ::
//                        new Point(-1, 1, 1) ::
//                        new Point(-gr, 0, 1 / gr) ::
//                        Nil,
//                        0.8, 0.2, new Color(1, 1, 1), new Point(0, 0, 0)
//                    ) ::

        shapes =
                    new Sphere(new Point(22, 5, 0), 3, 0.9, 0.1, ColorUtils.fromHex("E91E63")) :: //Pink 500
                    new Sphere(new Point(21, 9, -2), 1, 0.5, 0.5, ColorUtils.fromHex("673AB7")) :: //Deep Purple 500
                    new Sphere(new Point(36, -8, -2), 1, 0.5, 0.5, ColorUtils.fromHex("673AB7")) :: //Deep Purple 500
                    new Sphere(new Point(15, 18, -2), 1, 0.5, 0.5, ColorUtils.fromHex("673AB7")) :: //Deep Purple 500
                    new Sphere(new Point(10, -12, -2), 1, 0.5, 0.5, ColorUtils.fromHex("673AB7")) :: //Deep Purple 500
                    new Sphere(new Point(40, 12, -2), 1, 0.5, 0.5, ColorUtils.fromHex("673AB7")) :: //Deep Purple 500
                    new Sphere(new Point(3, -7, -2), 1, 0.5, 0.5, ColorUtils.fromHex("673AB7")) :: //Deep Purple 500
                    new Sphere(new Point(14, 0, -2), 1, 0.5, 0.5, ColorUtils.fromHex("673AB7")) :: //Deep Purple 500
                    new Sphere(new Point(80, 19, -2), 1, 0.5, 0.5, ColorUtils.fromHex("673AB7")) :: //Deep Purple
                    new Cuboid(
                        center = new Point(8, -10, -0.5),
                        XScale = 8, YScale = 2, ZScale = 5,
                        XRot = 0, YRot = 0, ZRot = TAU / 5,
                        diffusivity = 0.5, reflectivity = 0.5,
                        color = ColorUtils.fromHex("4CAF50")
                    ) ::
                    new Cuboid(
                        center = new Point(58, 25, -1.5),
                        XScale = 3, YScale = 3, ZScale = 3,
                        XRot = 0, YRot = 0, ZRot = -TAU / 5,
                        diffusivity = 0.35, reflectivity = 0.75,
                        color = ColorUtils.fromHex("009688")
                    ) ::
                    new CheckeredPlane(new Vector(0, 0, 1), 3, 0.8, 0.2, ColorUtils.fromHex("EEEEEE"), ColorUtils.fromHex("9E9E9E")) ::
                    Nil,

            lights =
                    if (iter <= 6 || true)
                        new PointLight(new Point(-10, 50, 80), 0.60) ::
                        new PointLight(new Point(-20, -50, 30), 0.30, ColorUtils.fromHex(if (iter > 4) "FFAB91" else "FFFFFF")) ::
                        Nil
                    else
                        new VolumeLight(new Point(-10, 50, 80), 9.0, 0.60).getPointLights :::
                        new VolumeLight(new Point(-20, -50, 30), 9.0, 0.30, ColorUtils.fromHex("FFAB91")).getPointLights :::
                        Nil,

            camera = new Camera(
                screenCentre = new Point(22, 5, 0), //(22, ...
                distanceFromScreen = 70, //40
                screenPixelDimensions = size,
                pointsPerPixel = 0.015,
                yaw = 0,
                pitch = TAU / 16,
                roll = 0
            ),

            enableShadows = iter > 2,
            enableDiffuse = iter > 1,
            enableReflections = iter > 3,
            ambientIntensity = if (iter > 1) 0.1 else 0.6,

            antiAliasingMode = if (iter > 5 && false) new AntiAliasingRegular(3) else new AntiAliasingNone,

            focusMode = if (iter > 7) new FocusSome(10, Math.PI / 4) else new FocusNone
        )
    }
}





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

