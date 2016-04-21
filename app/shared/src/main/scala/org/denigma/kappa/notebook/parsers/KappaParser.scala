package org.denigma.kappa.notebook.parsers
import fastparse.all._

/**
  * Created by antonkulaga on 24/03/16.
  */
class KappaParser extends CommentLinksParser
{
  import org.denigma.kappa.model.KappaModel._

  protected val text = P(digit | letter)

  protected val integer: P[Int] = P(
    "-".!.? ~ digit.rep(min = 1).!).map{
      case (None, str) => str.toInt
      case (Some(_), str) => - str.toInt
    }

  protected val name = P(
    (digit | letter | CharIn("_+-")).rep(min = 1).!
  )


  protected val normalNumber = P(
    integer ~ ("."~ integer).?
  ).map{
    case (i, None) => i.toDouble
    case (i, Some(o)) => (i + "." + o).toDouble
  }

  protected val eNumber = P( normalNumber ~ CharIn("Ee") ~ integer ).map{
    case (a, b) => a * Math.pow(10, b)
  }

  val number: P[Double] = P( eNumber | normalNumber )

  val textWithSymbols = P(digit | letter | CharIn("_!@$%^&*()_+=-.,/|?><`~"))

  val tokenDeclaration = P("%token:"~optSpaces~name)

  val label: P[String] = P("'"~(textWithSymbols | " ").rep(min = 1).! ~ "'")

  val labelOrNumber: P[Either[String, Double]] = P(label.map(l=>Left(l)) | number.map(n=>Right(n)))

  val linkLabel: P[String] = P( ("!" ~ text.rep(min = 1).!) | "!_".! | "?".!)

  val state: P[State] = P("~" ~ name).map(s=> State(s) )

  val side: P[Side] = P(name ~ state.rep ~ linkLabel.rep)
    .map{ case (n, states, links) => Side(n, states.toSet, links.toSet) }

  val agent: P[Agent] = P(name ~ "(" ~ side.? ~ (optSpaces ~ "," ~ optSpaces ~ side).rep ~ ")").map{
    case (n, sideOpt, sides2) => Agent(n,  sideOpt.map(List(_)).getOrElse(List.empty[Side]) ::: sides2.toList)
  }

  val agentDecl: P[Agent] = P(optSpaces ~ "%agent:" ~ optSpaces ~ agent)

  val rulePart: P[Pattern] = P(agent ~ (optSpaces ~ "," ~ optSpaces ~ agent).rep).map{
    case (ag, agents) =>
      val ags = ag::agents.toList
      val dist = ags.distinct
      val dupl = ags.diff(dist).zipWithIndex.map{case (value, i)=> value.copy(extra = i.toString)} //make agents unique
      Pattern(dist++dupl)
  }

  val coeffs: P[(Either[String, Double], Option[Either[String, Double]])] = P("@" ~optSpaces ~ labelOrNumber ~ (optSpaces ~ ","~ optSpaces ~labelOrNumber).?)

  val bothDirections = P("<->").map(v=>BothDirections)

  val left2right = P("->").map(v=>Left2Right)

  val right2left = P("<-").map(v=>Right2Left)

  val direction: P[Direction] = P(bothDirections | left2right | right2left)

  val rule = P(("'" ~ (textWithSymbols | " ").rep(min = 1).! ~ "'").? ~ spaces ~  rulePart ~ optSpaces ~ direction ~ optSpaces ~ rulePart ~ spaces ~ coeffs).map{
    case (n, left, BothDirections, right, (c1, c2opt)) => Rule(n.getOrElse(left + " "+Left2Right + " "+right), left, right, c1, c2opt)
    case (n, left, Left2Right, right, (c1, c2opt)) => Rule(n.getOrElse(left + " "+Left2Right + " "+right), left, right, c1, c2opt)
    case (n, left, Right2Left, right, (c1, c2opt)) => Rule(n.getOrElse(left + " "+Right2Left + " "+right), left, right, c1, c2opt) //TODO: fix coefficents
  }


  /*
  val rule = P("'" ~ text.! ~ "'" ~ rulePart ~ direction ~ rulePart ).map{
    case (name, left, BothDirections, right) => Rule(name, left, right, 0)
    case (name, left, Left2Right, right) => Rule(name, left, right, 0)
    case (name, left, Right2Left, right) => Rule(name, left, right, 0)
  }
  */


  //val leftSide =

}
