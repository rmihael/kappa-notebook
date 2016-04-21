package org.denigma.kappa.messages

import boopickle.Default._
import org.denigma.controls.charts.{LineStyles, Point, Series}


sealed trait KappaMessage

import scala.collection.immutable._

object KappaChart {
  lazy val empty = KappaChart(List.empty)

  implicit def fromKappaPlot(plot: KappaPlot): KappaChart = {
    val series = plot.legend.zipWithIndex.map{ case (title, i) =>
      //println("title: " + title)
      KappaSeries(title, plot.observables.map(o=> Point(o.time, o.values(i))).toList) }
    KappaChart(series.toList)
  }
}


case class KappaChart(series: List[KappaSeries])
{
  def isEmpty: Boolean = series.isEmpty
}

object KappaSeries {

  import scala.util.Random

  def randomColor() = s"rgb(${Random.nextInt(255)},${Random.nextInt(255)},${Random.nextInt(255)})"

  def randomLineStyle() = LineStyles(randomColor(), 4 ,"none" , 1.0)

}


case class KappaSeries(title: String, points: List[Point], style: LineStyles = KappaSeries.randomLineStyle()) extends Series

//sealed trait WebSocketMessage


case class Connected(username: String, channel: String /*, time: LocalDateTime = LocalDateTime.now()*/) extends KappaMessage

case class Disconnected(username: String, channel: String /*, time: LocalDateTime = LocalDateTime.now()*/) extends KappaMessage

trait ServerMessage extends KappaMessage
{
  def server: String
}

//case class Run(username: String, server: String, message: WebSim.RunModel, userRef: ActorRef, interval: FiniteDuration) extends ServerMessage

case class SimulationResult(server: String, simulationStatus: SimulationStatus, token:Int, initialParams: Option[RunModel] = None) extends ServerMessage

case class SyntaxErrors(server: String, errors: Array[String], initialParams: Option[RunModel] = None) extends ServerMessage

case class LaunchModel(server: String, parameters: RunModel) extends ServerMessage


object Code {
  def apply(lines: Seq[String]): Code = Code(lines.mkString("\n"))
}

case class Code(text: String) extends KappaMessage
{
  def isEmpty = text == ""

  lazy val lines = text.split("\n").toList

  def withInsertion(num: Int, part: String): Code = withInsertion(num, part.split("\n").toList)

  def withInsertion(num: Int, newLines: Seq[String]): Code = if(num > lines.length)

    Code(lines.take(num) ++ newLines)  else Code(lines.take(num) ++ newLines ++ lines.drop(num))

}

case class Load(project: KappaProject = KappaProject.default)  extends KappaMessage

import scala.collection.immutable.{List, Nil}

object KappaProject {
  lazy val default = KappaProject("repressilator", loaded = false)
}

case class KappaProject(name: String, loaded: Boolean/*, folder: KappaFolder = KappaFolder.empty*/) extends KappaMessage


object KappaPath{
  implicit val ordering = new Ordering[KappaPath] {
    override def compare(x: KappaPath, y: KappaPath): Int = x.path.compare(y.path) match {
      case 0 => x.hashCode().compare(y.hashCode()) //just to avoid annoying equality bugs
      case other => other
    }
  }

  lazy val empty = KappaFolder("", SortedSet.empty,  active = false)
}

object KappaFolder {
  lazy val empty = KappaFolder("", SortedSet.empty, active = false)
}

case class KappaFolder(path: String, children: SortedSet[KappaPath] = SortedSet.empty, active: Boolean = false) extends KappaPath
{
  lazy val childFiles = children.collect{case f: KappaFile =>f}
}

case class KappaFile(path: String, name: String, content: String, active: Boolean = false) extends KappaPath

trait KappaPath extends KappaMessage
{
  def path: String
  def active: Boolean
}