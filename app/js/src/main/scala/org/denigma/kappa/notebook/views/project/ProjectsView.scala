package org.denigma.kappa.notebook.views.project

import org.denigma.binding.binders.{Events, GeneralBinder}
import org.denigma.binding.views.{BindableView, CollectionSeqView, CollectionSortedSetView}
import org.denigma.kappa.messages.ProjectRequests.GetList
import org.denigma.kappa.messages._
import org.scalajs.dom
import org.scalajs.dom.MouseEvent
import org.scalajs.dom.raw.Element
import rx._
import rx.Ctx.Owner.Unsafe.Unsafe
import org.denigma.binding.extensions._

import scala.collection.immutable.{Seq, SortedSet}

class ProjectsView(val elem: Element,
                   val input: Var[KappaMessage],
                   val sender: Var[KappaMessage]) extends CollectionSortedSetView {

  val items: Var[SortedSet[KappaProject]] = Var(SortedSet.empty[KappaProject])


  override protected def subscribeUpdates() = {
    template.hide()
    this.items.now.foreach(i => this.addItemView(i, this.newItemView(i)) ) //initialization of views
    updates.onChange(upd=>{
      upd.removed.foreach(onRemove)
      upd.added.foreach(onInsert)
    })
  }

  val selectedProject = Var(KappaProject.empty)
  selectedProject.onChange{ proj => sender() = ProjectRequests.Load(proj) }

  input.onChange{

    case ProjectResponses.LoadedProject(proj) =>
      selectedProject() = proj

    case ProjectResponses.ProjectList(projects) =>
      items() = SortedSet(projects:_*)

    case org.denigma.kappa.messages.Done(cr: ProjectRequests.Create, _) =>
      println("project has been created, loading it...")
      sender() = ProjectRequests.Load(cr.project)

    case _=> //do nothing
  }

  val newProjectName = Var("")

  override def newItemView(item: Item): ItemView = constructItemView(item){
    case (el, _) => new ProjectTitleView(el, item, selectedProject, sender).withBinder(v=>new GeneralBinder(v))
  }

  override type Item = KappaProject
  override type ItemView = ProjectTitleView

  val createProjectClick: Var[MouseEvent] = Var(Events.createMouseEvent())

  createProjectClick.onChange{ ev=>
      sender() = ProjectRequests.Create(KappaProject(newProjectName.now), rewriteIfExists = false)
      newProjectName() = ""
  }

}

class ProjectTitleView(val elem: Element, val project: KappaProject, val selectProject: Var[KappaProject], toServer: Var[KappaMessage]) extends BindableView {

  val name: Var[String] = Var(project.name)

  val openClick: Var[MouseEvent] = Var(Events.createMouseEvent())
  openClick.triggerLater{
    selectProject() = project
  }

  val removeClick: Var[MouseEvent] = Var(Events.createMouseEvent())
  removeClick.triggerLater{
    val message = s"Do you really want to remove '${project.name}' project?"
    val confirmation = dom.window.confirm(message)
    if(confirmation) toServer() = KappaMessage.Container().andThen(ProjectRequests.Remove(project.name)).andThen(GetList)
  }

  val downloadClick: Var[MouseEvent] = Var(Events.createMouseEvent())
  downloadClick.triggerLater{
    toServer() = ProjectRequests.Download(project.name)
  }


  val current: Rx[Boolean] = selectProject.map(p=>p==project)


}