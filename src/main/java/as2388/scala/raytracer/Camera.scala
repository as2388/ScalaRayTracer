package as2388.scala.raytracer

class Camera(val screenCentre: Point, val distanceFromScreen: Float, val screenPixelDimensions: Size,
                val pointsPerPixel: Float, val yaw: Float, val pitch: Float, val roll: Float) {
    val halfWidth = screenPixelDimensions.width / 2
    val halfHeight = screenPixelDimensions.height / 2

    def lineToPixel(point: PixelPoint, yawChange: Float = 0, pitchChange: Float = 0) = {
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

class PixelPoint(val x: Float, val y: Float) {
    def scalarMultiply(constant: Float) = new Point(x * constant, y * constant, 0)

    def +(p: PixelPoint) = new PixelPoint(x + p.x, y + p.y)
}


