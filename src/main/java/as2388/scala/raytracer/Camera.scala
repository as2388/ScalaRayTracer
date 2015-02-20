package as2388.scala.raytracer

class Camera(val screenCentre: Point, val distanceFromScreen: Double, val screenPixelDimensions: Size,
                val pointsPerPixel: Double, val yaw: Double, val pitch: Double, val roll: Double) {
    val halfWidth = screenPixelDimensions.width / 2
    val halfHeight = screenPixelDimensions.height / 2

    def lineToPixel(point: PixelPoint, yawChange: Double = 0, pitchChange: Double = 0) = {
        val yaw = this.yaw + yawChange
        val pitch = this.pitch + pitchChange

        val viewDirection = (new Vector(1, 0, 0)  rotateY pitch) rotateZ yaw

        val viewUp = (((new Vector(0, 0, 1) rotateY pitch) rotateX roll) rotateZ yaw) normalize()
        val viewRight = viewDirection cross viewUp

        val screenPixelPoint = new PixelPoint(point.x - halfWidth, point.y - halfHeight) //point on screen in pixels
        val screenWorldPoint = screenPixelPoint scalarMultiply pointsPerPixel //point on screen in 2D, scaled to world co-ordinates

        val worldPoint = screenCentre +
                (viewRight * screenWorldPoint.x) +
                (viewUp * -screenWorldPoint.y)

        new Line(screenCentre + (viewDirection * -distanceFromScreen), worldPoint)
    }
}

class Size(val width: Int, val height: Int)

class PixelPoint(val x: Double, val y: Double) {
    def scalarMultiply(constant: Double) = new Point(x * constant, y * constant, 0)
}


