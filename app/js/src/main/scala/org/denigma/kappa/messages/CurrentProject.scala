package org.denigma.kappa.messages

import scala.collection.immutable.SortedSet


object CurrentProject {

  protected implicit def toMap(st: SortedSet[KappaFile]): Map[String, KappaFile] = st.map(f=> (f.name, f)).toMap

  def apply(name: String, place: String, allFiles: List[Map[String, KappaFile]] ): CurrentProject = {
    CurrentProject(name, place, allFiles.head,  allFiles(1),  allFiles(2),  allFiles(3),  allFiles(4), true)
  }

  def fromKappaProject(kappaProject: KappaProject) = {
    CurrentProject(kappaProject.name,
      kappaProject.folder.path,
      kappaProject.sourceMap,
      kappaProject.papers,
      kappaProject.images,
      kappaProject.videos,
      kappaProject.otherFiles,
      kappaProject.saved)
  }
}

case class CurrentProject(name: String,
                          path: String,
                          sourceMap: Map[String, KappaFile],
                          papers: Map[String, KappaFile],
                          images: Map[String, KappaFile],
                          videos: Map[String, KappaFile],
                          otherFiles: Map[String, KappaFile],
                          saved: Boolean
                         ) extends FileFilters
{

  lazy val allFilesList: List[Map[String, KappaFile]] = List(sourceMap, papers, images, videos, otherFiles)

  lazy val allFilesMap: Map[String, KappaFile] = sourceMap ++ papers ++ images ++ videos ++  otherFiles

  lazy val allFiles = SortedSet(allFilesMap.values.toSeq:_*)

  def toKappaProject: KappaProject = KappaProject(name, KappaFolder(path, SortedSet.empty, allFiles), saved)

  def removeByName(name: String): CurrentProject = {
    if(sourceMap.contains(name)) this.copy(sourceMap = sourceMap - name) else
    if(papers.contains(name)) this.copy(papers = papers - name) else
    if(images.contains(name)) this.copy(images = images - name) else
    if(videos.contains(name)) this.copy(videos = videos - name) else
      this.copy(otherFiles = otherFiles - name)
  }

  protected def update(fun: Map[String, KappaFile] =>  Map[String, KappaFile] ) = {
    val updated = for(mp <- allFilesList) yield fun(mp)
    CurrentProject.apply(name, path, updated)
  }

  def markSaved(files: Set[String]): CurrentProject = this.update{
    case mp =>  mp.map{ case (key, value)=> if(files.contains(key)) key->value.copy(saved = true) else key-> value}
  }

  def markSaved(files: Map[String, KappaFile]): CurrentProject = this.update{
    case mp =>  mp.map{
      case (key, value)=> if(files.contains(key)) key-> files(key) else key-> value}
  }

  def withRenames(renames: Map[String, (String, String)]) =  this.update{
    case mp =>  mp.map{
      case (key, value) if renames.contains(key)=>
        val (newName, newPath) = renames(key)
        newName -> value.copy(path = newPath)
      case (key, value)=> key -> value
    }
  }
}