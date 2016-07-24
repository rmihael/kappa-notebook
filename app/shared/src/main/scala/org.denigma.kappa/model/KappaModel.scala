package org.denigma.kappa.model

object KappaModel {

  trait KappaElement

  sealed trait Direction extends KappaElement
  case object Left2Right extends Direction
  case object Right2Left extends Direction
  case object BothDirections extends Direction

  object Link {
    implicit val ordering = new Ordering[Link] {
      override def compare(x: Link, y: Link): Int = x.label.compare(y.label) match {
        case 0 => x.hashCode().compare(y.hashCode())
        case other => other
      }
    }
  }

  case class Link(fromAgent: Agent, toAgent: Agent, fromSide: Sight, toSide: Sight, label: String) extends KappaNamedElement
  {
    def name = label

    require(fromAgent.sightSet.contains(fromSide), s"from Agent($fromAgent) should contain fromSide($fromSide)")

    require(toAgent.sightSet.contains(toSide), s"from Agent($toAgent) should contain fromSide($toSide)")

  }

  object Pattern {
    lazy val empty = Pattern(Nil)
  }

  case class Pattern(agents: List[Agent]) extends KappaElement
  {
    protected def isNamed(key: String) = key != "_" && key !="?"

    protected lazy val linkTuples: List[(String, (Sight, Agent))] = for{
      a <- agents
      (name, side) <-a.links
    } yield(name, (side, a))

    //TODO: think about a potential bug with two ? and _
    lazy val allLinks: Map[String, List[(Sight, Agent)]] = linkTuples.groupBy(_._1).map{ case (key, value) => key -> value.map(v=>v._2)}

    lazy val danglingLinks: Map[String, List[(Sight, Agent)]] = allLinks.collect{
      case (key, value) if value.length < 2 && isNamed(key) => key -> value
    }

    lazy val duplicatedLinks: Map[String, List[(Sight, Agent)]] = allLinks.collect{
      case (key, value) if value.length > 2 && isNamed(key) => key -> value
    }

    lazy val linksSomewhere = allLinks.getOrElse("_", List.empty)

    lazy val unclearLinks = allLinks.getOrElse("?", List.empty)

    lazy val links: Map[String, Link] = allLinks.collect {
      case (key, (side1, agent1)::(side2, agent2)::Nil)  =>
        key -> KappaModel.Link(agent1, agent2, side1, side2, key)
    }

    private def sameAgent(one: Agent, two: Agent): Boolean = {
      one.name==two.name && one.sightNames == two.sightNames
    }

    def sameAgents(pat: Pattern): List[(Agent, Agent)] = agents.zip(pat.agents).takeWhile(ab=>sameAgent(ab._1, ab._2))

  }

  case class Rule(name: String, left: Pattern, right: Pattern, forward: Either[String, Double], backward: Option[Either[String, Double]] = None) extends KappaNamedElement
  {
    lazy val same = left.sameAgents(right)

    lazy val removed = if(same.length==left.agents.length) Nil else left.agents.takeRight(left.agents.length - same.length)

    lazy val added = if(same.length==right.agents.length) Nil else right.agents.takeRight(right.agents.length - same.length)

    lazy val modified = same.filter{
      case (one, two)=> one.sightSet != two.sightSet
    }

    lazy val modifiedLeft = modified.map(_._1)

    lazy val modifiedRight = modified.map(_._1)

    def direction: Direction = if(backward.isEmpty) KappaModel.Left2Right else KappaModel.BothDirections
    //lazy val kept = right
    //def atomic: Boolean = added.size==1 || removed.size == 1
  }

  trait KappaNamedElement {
    def name: String
  }

  case class State(name: String) extends KappaNamedElement

  case class Sight(name: String, states: Set[State] = Set.empty, links: Set[String] = Set.empty) extends KappaNamedElement {
    def ~(state: State): Sight = {
      copy(states = states + state)
    }

  }
  object Agent {
    implicit val ordering = new Ordering[Agent] {
      override def compare(x: Agent, y: Agent): Int = x.name.compare(y.name) match {
        case 0 => x.hashCode().compare(y.hashCode()) //just to avoid annoying equality bugs
        case other => other
      }
    }
  }

  case class Agent(name: String, sights: List[Sight] = List.empty, extra: String = "") extends KappaNamedElement
  {
    lazy val sightSet = sights.toSet

    lazy val sightNames = sights.map(s=>s.name)

    lazy val links: List[(String, Sight)] = for {
        s <- sights
        l <- s.links
      } yield l -> s

    lazy val linkMap = links.toMap

    def hasDuplicates = links.size > linkMap.size
  }

}


