package org.denigma.kappa.notebook.views.simulations.fluxes

import org.denigma.binding.extensions._
import org.denigma.binding.views.{CollectionSortedSetView, UpdatableView}
import org.denigma.controls.code.CodeBinder
import org.denigma.kappa.messages.WebSimMessages.FluxMap
import org.denigma.kappa.notebook.graph.{Colors, KappaEdgeVisualSettings, KappaNodeVisualSettings, LineParams}
import org.scalajs.dom
import org.scalajs.dom.raw.Element
import org.scalajs.dom.svg.SVG
import rx.Ctx.Owner.Unsafe.Unsafe
import rx._

import scala.collection.immutable.SortedSet

class FluxView(val elem: Element, val name: String, val item: Var[FluxMap], val tab: Rx[String]) extends CollectionSortedSetView
{

  type Item = RuleFlux

  override type ItemView = HitsView

  lazy val container = elem.selectByClass("graph")

  val fluxName: Rx[String] =  item.map(fl=>fl.flux_name)
  val start = item.map(fl=>fl.flux_begin_time)
  val end = item.map(fl=>fl.flux_begin_time)
  val other = item.map(fl => fl.flux_rules)
  val hits = item.map(fl => fl.flux_hits)
  val fluxes = item.map(fl => fl.flux_fluxs)


  val active = tab.map(t=>t==name)

  override val items: Rx[SortedSet[RuleFlux]] = item.map(i => RuleFlux.fromFluxMap(i))
  items.foreach(i=>println("items are = "+items.now))

  override def newItemView(item: Item): ItemView = this.constructItemView(item){
    case (el, _) => new HitsView(el, item).withBinder(v => new CodeBinder(v))
  }

  val s =dom.document.getElementById("canvas") match {
    case s: SVG => s
    case other => throw new Exception("cannot find SVG canvas from FluxView")
  }

  override lazy val injector = defaultInjector
    .register("FluxGraphView") { case (el, args) =>
      new FluxGraphView(el, items, new KappaNodeVisualSettings(14, 3), new KappaEdgeVisualSettings(8, 2, LineParams(Colors.green)), s).withBinder(v => new CodeBinder(v))
    }
}