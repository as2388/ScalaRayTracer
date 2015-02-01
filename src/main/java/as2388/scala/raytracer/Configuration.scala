package as2388.scala.raytracer


class Configuration(
       val imageSize: Size,
       val shapes: List[Shape],
       val lights: List[PointLight],
       val camera: Camera,
       val singularities: List[Singularity] = Nil,
       val singularityDepthLimit: Double = 100)