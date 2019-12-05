package smartrocket

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.effect.DropShadow
import scalafx.scene.layout.HBox
import scalafx.scene.paint.Color._
import scalafx.scene.paint._
import scalafx.scene.text.Text
import scalafx.scene.shape._
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ListBuffer
import scalafx.animation._
import scala.util.Random
import scalafx.collections.ObservableBuffer
import javafx.collections.{ListChangeListener, ObservableList}
import scala.util.Try
import scalafx.scene.layout.{BorderPane, Pane}

case class Rocket(var size: Int,
  var vel: Main.Vector,
  var acc: Main.Vector,
  var pos: Main.Vector,
  var completed: Boolean,
  var crashed: Boolean,
  circle: Circle,
  var dna: Dna,
  var count: Int,
  var fitness: Double,
  var duration: Double,
  createTime: Double)

object Rocket {

    def createRocket(dna:Dna):Rocket={
      val rocket = new Rocket(10, new Main.Vector(), new Main.Vector(),
      new Main.Vector(250, 500), false, false,
      Circle(0, 0, 10), new Dna(), 0, 0.0,
      20.0, System.nanoTime)
      rocket.dna = dna
      rocket
    }

    def run(rocket: Rocket):Unit = {
      Rocket.update(rocket)
    }

    def computeFitness(target: Main.Target, rocket: Rocket):Double = {
      var d = Rocket.dist(rocket.pos.x, rocket.pos.y, target.x, target.y)
      d = 1/d
      if(rocket.completed){
        d *= 10

        rocket.duration = (System.nanoTime - rocket.createTime) / 1e9d // divide by 1e9d because it is in nano second, d stand for double
      }
      else if(rocket.crashed){
        d/=10
      }
      d
    }

    def applyForce(acc: Main.Vector, force:Main.Vector):(Double, Double) = {
      val (acc_x, acc_y) = add(acc.x, acc.y, force.x, force.y)
      (acc_x, acc_y)
    }

    def mult(x1:Double, y1:Double, n:Double):(Double, Double) = {
      val r1 = x1 * n
      val r2 = y1 * n

      (r1, r2)
    }

    def add(x1:Double, y1:Double, x2:Double, y2:Double):(Double, Double) = {
      val r1 = x1 + x2
      val r2 = y1 + y2

      (r1, r2)
    }

    def dist(x1:Double, y1:Double, x2:Double, y2:Double):Double={
      var d = Math.sqrt((x2 - x1)* (x2 -x1) + (y2-y1) * (y2-y1))
      d
    }

    def update(rocket:Rocket):Unit = {

      if(Rocket.dist(rocket.pos.x, rocket.pos.y, Main.target.x, Main.target.y)  < 25){
        rocket.completed = true
      }

      if(rocket.pos.x + rocket.size >= Main.obstacle.x &&
        rocket.pos.x <= Main.obstacle.x + Main.obstacle.width + rocket.size &&
        rocket.pos.y + rocket.size >= Main.obstacle.y &&
        rocket.pos.y <= Main.obstacle.y + Main.obstacle.height + rocket.size){
          rocket.crashed = true
        }
        if(rocket.pos.x < 0 || rocket.pos.x > Main.width-5 || rocket.pos.y > Main.height || rocket.pos.y < 0){
          rocket.crashed = true
        }

        if(!rocket.completed && !rocket.crashed) {

          val (acc_x, acc_y) = Rocket.applyForce(rocket.acc, rocket.dna.genes(rocket.count))

          rocket.acc.x = acc_x
          rocket.acc.y = acc_y

          rocket.count = (rocket.count + 1)%Main.dna_length

          val (vel_x, vel_y) = Rocket.add(rocket.vel.x, rocket.vel.y, rocket.acc.x, rocket.acc.y)
          rocket.vel.x = vel_x
          rocket.vel.y = vel_y

          val (pos_x, pos_y) = Rocket.add(rocket.pos.x, rocket.pos.y, rocket.vel.x, rocket.vel.y)
          rocket.pos.x = pos_x
          rocket.pos.y = pos_y

          val (acc_x_m, acc_y_m) = Rocket.mult(rocket.acc.x, rocket.acc.y, 0.0)
          rocket.acc.x = acc_x_m
          rocket.acc.y = acc_y_m

          val limit = 3
          if(rocket.vel.x > limit){
            rocket.vel.x = limit
          }
          if(rocket.vel.x < -limit){
            rocket.vel.x = -limit
          }
          if(rocket.vel.y > limit){
            rocket.vel.y = limit
          }
          if(rocket.vel.y < -limit){
            rocket.vel.y = -limit
          }
        }
      }
    }
