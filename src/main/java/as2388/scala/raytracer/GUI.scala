package as2388.scala.raytracer

import java.awt.event.ActionListener
import java.awt.image.BufferedImage
import java.awt.{event, Dimension, Graphics2D}
import java.io.File
import java.util.concurrent.{Executor, TimeUnit}
import javax.imageio.ImageIO
import javax.swing.Timer

import as2388.scala.raytracer.examples.{LenseConfiguration, RollingConfiguration, CheckerboardConfiguration}

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.{ExecutionContext, Future}
import scala.swing.event.ActionEvent
import scala.swing.{MainFrame, Panel, SimpleSwingApplication, Swing}
import scalafx.application.Platform

object CommandLine {
    def main(args: Array[String]) = {
        println("Starting RayTracer...")

        val time = System.currentTimeMillis()

        val size = new Size(3200, 1800)

        val bi: BufferedImage = new BufferedImage(3200, 1800, 1)

        //        val tracer = new RayTracer(new CheckerboardConfiguration(size, 0, 0).getConfiguration)
        //        tracer writeToImage (bi, 0, 0, 3200, 1800)

        val file = new File("test.png")

        println("Writing to file 'test.png'")

        ImageIO.write(bi, "png", file)

        val millis = System.currentTimeMillis() - time
        println("Total time: " +
                TimeUnit.MILLISECONDS.toHours(millis) + ":" +
                TimeUnit.MILLISECONDS.toMinutes(millis) % 60 + ":" +
                TimeUnit.MILLISECONDS.toSeconds(millis) % 60
        )

        println("Done")
    }
}

object GUI extends SimpleSwingApplication {
    var time = System.currentTimeMillis()
    println("Ray Tracing...")

    val TAU = 2 * Math.PI

    val size = new Size(1920, 1080)
    val sizeX = 80
    val sizeY = 90

    val image: BufferedImage = new BufferedImage(size.width, size.height, 1)
    val total: Float = size.width / sizeX * size.height / sizeY
    var remaining = total

    val frame = 184
    val frameStep = 4

    val timer = new Timer(1000, taskPerformer)
    timer.start()

    run(frame * TAU.toFloat / 400, frame)

    def run(cameraRot: Float, frame: Int): Unit = {
        timer.restart()
//        val tracer = new RayTracer(new CheckerboardConfiguration(size, 8, frame).getConfiguration.configurations.head)
        val tracer = new AnimatedRayTracer(new LenseConfiguration(size, frame).getConfiguration)
        for (x <- 0 to size.width / sizeX - 1; y <- 0 to size.height / sizeY - 1) yield {
            val b = Future {
                val mImage = new BufferedImage(1920, 1080, 1)
                tracer writeToImage(mImage, x * sizeX, y * sizeY, sizeX, sizeY)
                (mImage, x * sizeX, y * sizeY)
            }

            b onFailure {
                case result =>
                    result.printStackTrace()
            }

            b onSuccess {
                case result => Swing.onEDT {
                    val g = image.getGraphics
                    g.drawImage(result._1,
                        result._2, result._3, result._2 + sizeX, result._3 + sizeY,
                        result._2, result._3, result._2 + sizeX, result._3 + sizeY,
                        null)
                    top.repaint()

                    this.synchronized {
                        remaining = remaining - 1
                    }

                    if (remaining == 0) {
                        remaining = total
                        println("Total time: " + formattedElapsedTime)
                        val file = new File("b" + frame + ".png")
                        ImageIO.write(image, "png", file)
                        time = System.currentTimeMillis()
                        if (cameraRot < TAU)
                            run(cameraRot, frame + frameStep)
                    }
                }
            }
        }
    }

    val top = new MainFrame {
        title = "Initialising"
        preferredSize = new Dimension(1920,1080)
        contents = new Panel {
            override def paintComponent(g: Graphics2D): Unit = {
                g.drawImage(image, 0, 0, null)
            }
        }
    }

    def formattedElapsedTime: String = {
        val millis = System.currentTimeMillis() - time
        TimeUnit.MILLISECONDS.toHours(millis) + ":" +
                TimeUnit.MILLISECONDS.toMinutes(millis) % 60 + ":" +
                TimeUnit.MILLISECONDS.toSeconds(millis) % 60
    }

    def taskPerformer: ActionListener = new ActionListener {
        override def actionPerformed(actionEvent: event.ActionEvent): Unit = {
            if (remaining == 0) {
                timer.stop()
            }

            val millis = ((System.currentTimeMillis() - time) / ((total - remaining) / total)).toLong - (System.currentTimeMillis() - time)
            val predictedRemaining = TimeUnit.MILLISECONDS.toHours(millis) + ":" +
                    TimeUnit.MILLISECONDS.toMinutes(millis) % 60 + ":" +
                    TimeUnit.MILLISECONDS.toSeconds(millis) % 60

            top.title = (((total - remaining) / total) * 10000).floor / 100 + "% - " +
                    formattedElapsedTime + " - " + predictedRemaining + " - Ray Tracer"
        }
    }
}