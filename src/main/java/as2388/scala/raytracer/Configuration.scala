package as2388.scala.raytracer

import as2388.scala.raytracer.shapes.Shape

class Configuration(
       val imageSize: Size,
       val shapes: List[Shape],
       val lights: List[PointLight],
       val camera: Camera,
       val singularities: List[Singularity] = Nil,
       val singularityDepthLimit: Double = 100,
       val enableShadows: Boolean = true,
       val antiAliasingMode: AntiAliasingMode = new None)

abstract class AntiAliasingMode
case class None() extends AntiAliasingMode
case class Regular(count: Int) extends AntiAliasingMode