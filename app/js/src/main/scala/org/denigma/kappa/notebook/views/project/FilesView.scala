package org.denigma.kappa.notebook.views.project

import org.denigma.binding.views.{BindableView, ItemsSeqView, ItemsSetView}
import org.denigma.controls.code.CodeBinder
import org.denigma.kappa.messages.KappaPath
import org.scalajs.dom.raw.Element
import rx._
import rx.Ctx.Owner
import rx.Rx.Dynamic

import scala.collection.immutable.SortedSet

class FilesView(val elem: Element, path: Rx[KappaPath]) extends BindableView with ItemsSeqView {

  override type Item = Var[KappaPath]

  override type ItemView = TestFilesView

  protected def wrap(p: KappaPath)(implicit ctx: Ctx.Owner): Item = Var.apply(p)

  val items: Rx[List[Item]] = Rx.unsafe{
    val p = path()
    for (child <- p.children) yield wrap(p)
  }

  import rx.Ctx.Owner.Unsafe.Unsafe

  val name = path.map(p=>p.path)

  override def newItemView(item: Item): ItemView = this.constructItemView(item){
    case (el, params) =>
      println(s"add chile ${item.now} ###ALL ARE: = ${items.now.map(_.now).toList.mkString(" | ")}")
      new TestFilesView(el, item).withBinder(v => new CodeBinder(v))
  }

}

class TestFilesView(val elem: Element, path: Rx[KappaPath]) extends BindableView  {
  println(s"child created, path is ${path.now.path}")
}