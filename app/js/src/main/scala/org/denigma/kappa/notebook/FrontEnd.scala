package org.denigma.kappa.notebook

import org.denigma.binding.binders.{GeneralBinder, NavigationBinder}
import org.denigma.binding.extensions.sq
import org.denigma.binding.views.BindableView
import org.denigma.controls.code.{CodeBinder}
import org.denigma.controls.login.{AjaxSession, LoginView}
import org.denigma.kappa.notebook.views.NotebookView
import org.scalajs.dom
import org.scalajs.dom.UIEvent
import org.scalajs.dom.raw.{PopStateEvent, Element, HTMLElement}
import rx.Ctx.Owner.Unsafe.Unsafe
import org.denigma.binding.extensions._
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.JSConverters._

/**
 * Just a simple view for the whole app, if interested ( see https://github.com/antonkulaga/scala-js-binding )
 */
@JSExport("FrontEnd")
object FrontEnd extends BindableView with scalajs.js.JSApp
{

  override lazy val id: String = "body"

  lazy val elem: Element = dom.document.body

  val session = new AjaxSession()

  class Scroller(val elem: HTMLElement, target: HTMLElement) extends BindableView{
    import scalatags.JsDom.all._

    protected def popStateHandler(ppe: PopStateEvent): Unit = {
      println("POP STATE CHANGED")
      val uid: js.UndefOr[Dynamic] = ppe.state.dyn.id
      uid.toOption match {
        case None | Some(null)=> println("non or null")
        case st =>
          val gid = st.toString
          scrollTo(gid)
      }
    }

    override def bindView() = {
      super.bindView()
      val child = p( name := "insider", width := target.scrollWidth, br()).render
      elem.appendChild(child)
      println(child.outerHTML + " "+target.scrollWidth)
      elem.style.overflowX = "scroll"
      elem.onscroll = {e: UIEvent =>
        //println("scrolllefft = "+ elem.scrollLeft)
        if(target.scrollLeft != elem.scrollLeft) target.scrollLeft = elem.scrollLeft
      }
      target.onscroll = {e: UIEvent =>
        //println("scrollleft =" + target.scrollLeft)
        elem.scrollLeft = target.scrollLeft
      }
      dom.window.onpopstate = popStateHandler _
    }

  }

  protected def scrollTo(ident: String) = {
    println("scroll to "+ident)
    for{
      e <- sq.byId(ident)
    } dom.window.scrollTo(e.offsetLeft.toInt, 0)
  }

  /**
   * Register views
   */
  override lazy val injector = defaultInjector
    .register("Login")((el, args) => new LoginView(el, session).withBinder(new GeneralBinder(_)))
    .register("Notebook")((el, args) => new NotebookView(el, session).withBinder(n => new CodeBinder(n)))
    .register("Scroller") {
      case (el: HTMLElement, args) if args.contains("target")=>
        val name = args("target").toString
        val target = sq.byId(name).get
        new Scroller(el, target).withBinder(n => new GeneralBinder(n))
    }


  this.withBinders(me => List(new GeneralBinder(me), new NavigationBinder(me)))

  @JSExport
  def main(): Unit = {
    this.bindView()
  }

  @JSExport
  def load(content: String, into: String): Unit = {
    dom.document.getElementById(into).innerHTML = content
  }

  @JSExport
  def moveInto(from: String, into: String): Unit = {
    for {
      ins <- sq.byId(from)
      intoElement <- sq.byId(into)
    } {
      this.loadElementInto(intoElement, ins.innerHTML)
      ins.parentNode.removeChild(ins)
    }
  }

}
