package org.denigma.kappa.notebook.graph.layouts

import org.denigma.kappa.notebook.graph.drawing.Rectangle
import org.denigma.threejs.{PerspectiveCamera, Vector3}
import org.scalajs.dom


class Attraction[Node <: ForceNode, Edge <: ForceEdge](val attractionMult: Double, EPSILON: Double = 0.001)
                                                      (val compare: (Edge#FromNode, Edge#ToNode) => (Double, Double) )
  extends Force[Node, Edge] {


  override def tick(width: Double, height: Double, camera: PerspectiveCamera, nodes: Vector[Node], edges: Vector[Edge], forceConstant: Double) = {
    val attractionGlobal = attractionMult * forceConstant
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
      val (m1, m2) = compare(edge.from, edge.to)

      val force1= distance  / (attractionGlobal * m2)
      val force2 = distance  / (attractionGlobal * m1)

      l1.force -= force1
      l2.force += force2

      l1.offset.x -= deltaX  * force1
      l1.offset.y -= deltaY  * force1
      l1.offset.z -= deltaZ  * force1


      l2.offset.x += deltaX  * force2
      l2.offset.y += deltaY  * force2
      l2.offset.z += deltaZ  * force2
    }
  }

}



case class SpringParams(length: Double, strength: Double = 1, mass1: Double = 1, mass2: Double = 1)

class SpringForce[Node <: ForceNode, Edge <: ForceEdge](val springMult: Double, EPSILON: Double = 0.001)
                                                  (val compute: (Edge) => SpringParams )
  extends Force[Node, Edge] {


  override def tick(width: Double, height: Double, camera: PerspectiveCamera, nodes: Vector[Node], edges: Vector[Edge], forceConstant: Double) = {
    val attractionGlobal = springMult * forceConstant
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
      //val (m1, m2) = compare(edge.from, edge.to)
      val SpringParams(length, strength, mass1, mass2) = compute(edge)

      val force1= (distance - length)  * strength * mass2 * attractionGlobal / 2
      val force2 = (distance - length) * strength * mass1 *  attractionGlobal / 2
      //println(s"SPRING = (distance(${distance}) - length(${length}))  * springMult($springMult} * forceConstant ${forceConstant} = $force1")


      l1.force -= force1
      l2.force += force2

      l1.offset.x -= deltaX / distance  * force1
      l1.offset.y -= deltaY / distance  * force1
      l1.offset.z -= deltaZ  / distance * force1


      l2.offset.x += deltaX / distance  * force2
      l2.offset.y += deltaY / distance * force2
      l2.offset.z += deltaZ /distance * force2
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
class BorderForce[Node <: ForceNode, Edge <: ForceEdge](val repulsionMult: Double, val threshold: Double, mult: Double, center: Vector3, EPSILON: Double = 0.1) extends Force[Node, Edge] {

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


  override def tick(width: Double, height: Double, camera: PerspectiveCamera, nodes: Vector[Node], edges: Vector[Edge], forceConstant: Double) = {
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

      val distance = Math.max(Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2)), EPSILON)
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

class Gravity[Node <: ForceNode, Edge <: ForceEdge](val gravityMult: Double, center: Vector3,  EPSILON: Double = 0.01) extends Force[Node, Edge] {

  override def tick(width: Double, height: Double, camera: PerspectiveCamera, nodes: Vector[Node], edges: Vector[Edge], forceConstant: Double) = {
    val attraction = gravityMult * forceConstant
    for {i <- nodes.indices}
    {
      val no1 = nodes(i)
      val l1 = no1.layoutInfo
      if(i==0) l1.setOffsets(0, 0, 0)

      l1.force = 0
      l1.init(no1.position)

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


class Repulsion[Node <: ForceNode, Edge <: ForceEdge](val repulsionMult: Double, EPSILON: Double = 0.1)
                                                     (compareRepulstion: (Node, Node) => (Double, Double))
  extends Force[Node, Edge] {


  override def tick(width: Double, height: Double, camera: PerspectiveCamera, nodes: Vector[Node], edges: Vector[Edge], forceConstant: Double) = {
    val repulsion = repulsionMult * forceConstant
    for {i <- nodes.indices}
    {
      val no1 = nodes(i)
      //val n1 = no1.view
      val l1 = no1.layoutInfo
      if(i==0) l1.setOffsets(0, 0, 0)

      l1.force = 0
      l1.init(no1.position)

      for {j <- (i + 1) until  nodes.size; if i != j} {
        val no2 = nodes(j)
        val l2 = no2.layoutInfo
        l2.init(no2.position)

        val deltaX = l1.pos.x - l2.pos.x
        val deltaY = l1.pos.y - l2.pos.y
        val deltaZ = l1.pos.z - l2.pos.z
        val (m1, m2) = compareRepulstion(no1, no2)

        val distance = Math.max(EPSILON * 5, l1.pos.distanceTo(l2.pos))
        val distSquared  = Math.pow(distance, 2)
        val force1 =  (repulsion * repulsion) * m2 / distSquared
        //println(s"t distance ${distance}: (repulsion(${repulsion}) * repulsion(${repulsion})) * m1(${m2}) / distSquared(${distSquared})) = $force1")
        l1.force += force1
        l1.offset.x = l1.offset.x + (deltaX / distance) * force1
        l1.offset.y = l1.offset.y + (deltaY / distance) * force1
        l1.offset.z = l1.offset.z + (deltaZ / distance) * force1

        if(i==0){
          l2.setOffsets(0,0,0)
        }

        val force2 = (repulsion * repulsion)  * m1 / distSquared

        l2.force += force2
        l2.offset.x = l2.offset.x - (deltaX / distance) * force2
        l2.offset.y = l2.offset.y - (deltaY / distance) * force2
        l2.offset.z = l2.offset.z - (deltaZ / distance) * force2
      }
    }
  }

}