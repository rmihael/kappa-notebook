package org.denigma.kappa.notebook.views.visual.rules.layouts

import org.denigma.kappa.notebook.views.visual.rules.drawing.Rectangle
import org.denigma.kappa.notebook.views.visual.rules.layouts.LayoutMode.LayoutMode
import org.denigma.kappa.notebook.views.visual.rules.{AgentNode, KappaEdge}
import org.denigma.kappa.notebook.views.visual.utils.Randomizable
import org.denigma.threejs.{PerspectiveCamera, Vector3}
import rx._


class Attraction(val attractionMult: Double, EPSILON: Double = 0.00001) extends Force[AgentNode, KappaEdge] {


  override def tick(width: Double, height: Double, camera: PerspectiveCamera, nodes: Vector[AgentNode], edges: Vector[KappaEdge], forceConstant: Double) = {
    val attraction = attractionMult * forceConstant
    for {i <- edges.indices} {
      val edge = edges(i)
      //val l1 = edge.from.view.layout
      //val l2 = edge.to.view.layout
      val l1 = edge.from.layoutInfo
      val l2 = edge.to.layoutInfo

      val deltaX = l1.pos.x - l2.pos.x
      val deltaY = l1.pos.y - l2.pos.y
      val deltaZ = l1.pos.z - l2.pos.z

      val distance = Math.max(EPSILON, l1.pos.distanceTo(l2.pos))

      val force = distance  / attraction

      l1.force -= force
      l2.force += force

      l1.offset.x -= deltaX  * force
      l1.offset.y -= deltaY  * force
      l1.offset.z -= deltaZ  * force


      l2.offset.x += deltaX  * force
      l2.offset.y += deltaY  * force
      l2.offset.z += deltaZ  * force
    }
  }

}
/*
class BorderForce(val repulsionMult: Double, val threshold: Double, mult: Double, center: Vector3) extends Force[KappaNode, KappaEdge] {

  def border(width: Double, height: Double) = Rectangle.fromCorners(center.x - width / 2, center.y - height / 2, center.x + width / 2, center.y + height / 2)

  def toHorBorders(rect: Rectangle, x: Double) = (x - rect.left, rect.right - x)

  def toVerBorders(rect: Rectangle, y: Double) = (y - rect.top,  rect.bottom - y)

  def toBorder: PartialFunction[(Double, Double), Double] = {
    case (ld, rd) if (ld - threshold) < 0.0  =>
      ld - threshold

    case (ld, rd) if (rd - threshold) < 0.0  =>
      Math.abs(rd - threshold)

    case _ => 0
  }


  override def tick(width: Double, height: Double, camera: PerspectiveCamera, nodes: Vector[KappaNode], edges: Vector[KappaEdge], forceConstant: Double) = {
    val repulsion = repulsionMult * forceConstant
    val rect = border(width *  mult, height * mult)
    for {
      i <- nodes.indices
    }
    {
      val no1 = nodes(i)
      val l1 = no1.layoutInfo
      if(i==0) l1.setOffsets(0, 0, 0)

      val deltaX = toBorder(toHorBorders(rect, l1.pos.x))
      val deltaY = toBorder(toVerBorders(rect, l1.pos.y))
      val deltaZ = 0

      val distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2))
      if(distance > 0) {
        val force =  (repulsion * repulsion) / Math.pow(distance, 2)
        l1.force += force
        l1.offset.x = l1.offset.x - (deltaX / distance) * force
        l1.offset.y = l1.offset.y - (deltaY / distance) * force
        l1.offset.z = 0
      }
    }
  }

}
*/
class BorderForce(val repulsionMult: Double, val threshold: Double, mult: Double, center: Vector3) extends Force[AgentNode, KappaEdge] {

  def border(width: Double, height: Double) = Rectangle.fromCorners(center.x - width / 2, center.y - height / 2, center.x + width / 2, center.y + height / 2)

  def toHorBorders(rect: Rectangle, x: Double) = (x - rect.left, rect.right - x)

  def toVerBorders(rect: Rectangle, y: Double) = (y - rect.top,  rect.bottom - y)

  def toBorder: PartialFunction[(Double, Double), Double] = {
    case (ld, rd) if (ld - threshold) < 0.0  =>
      ld - threshold

    case (ld, rd) if (rd - threshold) < 0.0  =>
      Math.abs(rd - threshold)

    case _ => 0
  }


  override def tick(width: Double, height: Double, camera: PerspectiveCamera, nodes: Vector[AgentNode], edges: Vector[KappaEdge], forceConstant: Double) = {
    val repulsion = repulsionMult * forceConstant
    val rect = border(width *  mult, height * mult)
    for {
      i <- nodes.indices
    }
    {
      val no1 = nodes(i)
      val l1 = no1.layoutInfo
      if(i==0) l1.setOffsets(0, 0, 0)

      val deltaX = toBorder(toHorBorders(rect, l1.pos.x))
      val deltaY = toBorder(toVerBorders(rect, l1.pos.y))
      val deltaZ = 0

      val distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2))
      if(distance > 0) {
        val force =  (repulsion * repulsion) / Math.pow(distance, 2)
        l1.force += force
        l1.offset.x = l1.offset.x - (deltaX / distance) * force
        l1.offset.y = l1.offset.y - (deltaY / distance) * force
        l1.offset.z = 0
      }
    }
  }

}

class Gravity(val attractionMult: Double, val gravityMult: Double, center: Vector3,  EPSILON: Double = 0.00001) extends Force[AgentNode, KappaEdge] {

  override def tick(width: Double, height: Double, camera: PerspectiveCamera, nodes: Vector[AgentNode], edges: Vector[KappaEdge], forceConstant: Double) = {
    val attraction = attractionMult * forceConstant
    for {i <- nodes.indices}
    {
      val no1 = nodes(i)
      val n1 = no1.view
      val l1 = no1.layoutInfo
      if(i==0) l1.setOffsets(0, 0, 0)

      l1.force = 0
      l1.init(n1.position)

      val deltaX = l1.pos.x - center.x
      val deltaY = l1.pos.y - center.y
      val deltaZ = l1.pos.z - center.z

      val distance = Math.max(EPSILON, l1.pos.distanceTo(center))

      val force =  attraction * gravityMult / Math.sqrt(distance)

      l1.force += force
      l1.offset.x = l1.offset.x - (deltaX / distance) * force
      l1.offset.y = l1.offset.y - (deltaY / distance) * force
      l1.offset.z = l1.offset.z - (deltaZ / distance) * force
    }
  }

}


class Repulsion(val repulsionMult: Double, EPSILON: Double = 0.00001) extends Force[AgentNode, KappaEdge] {

  override def tick(width: Double, height: Double, camera: PerspectiveCamera, nodes: Vector[AgentNode], edges: Vector[KappaEdge], forceConstant: Double) = {
    val repulsion = repulsionMult * forceConstant
    for {i <- nodes.indices}
    {
      val no1 = nodes(i)
      val n1 = no1.view
      val l1 = no1.layoutInfo
      if(i==0) l1.setOffsets(0, 0, 0)

      l1.force = 0
      l1.init(n1.position)

      for {j <- (i + 1) until  nodes.size; if i != j} {
        val no2 = nodes(j)
        val n2 = no2.view
        val l2 = no2.layoutInfo
        l2.init(n2.position)

        val deltaX = l1.pos.x - l2.pos.x
        val deltaY = l1.pos.y - l2.pos.y
        val deltaZ = l1.pos.z - l2.pos.z

        val distance = Math.max(EPSILON, l1.pos.distanceTo(l2.pos))


        val force =  (repulsion * repulsion) / Math.pow(distance, 2)
        l1.force += force
        l1.offset.x = l1.offset.x + (deltaX / distance) * force
        l1.offset.y = l1.offset.y + (deltaY / distance) * force
        l1.offset.z = l1.offset.z + (deltaZ / distance) * force

        if(i==0){
          l2.setOffsets(0,0,0)
        }

        l2.force += force
        l2.offset.x = l2.offset.x - (deltaX / distance) * force
        l2.offset.y = l2.offset.y - (deltaY / distance) * force
        l2.offset.z = l2.offset.z - (deltaZ / distance) * force
      }
    }
  }

}