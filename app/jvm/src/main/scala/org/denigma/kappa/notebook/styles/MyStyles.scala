package org.denigma.kappa.notebook.styles

import scalacss.Defaults._


/**
  * Created by antonkulaga on 06/04/16.
  */
object MyStyles extends TextLayerStyles with DragDropStyles with TabGridsStyles with ListStyles
{
  import dsl._

  val totalWidth = 5120 px

  "#Notebook" -(
    maxHeight(95 vh),
    minWidth(totalWidth)
    )

  "#Scroller" -(
    minWidth(totalWidth)
    )

  "#main" -(
    overflowX.auto,
    overflowY.hidden,
    maxHeight(98 vh)
    )

  "#grid" -(
    overflowX.auto,
    overflowY.hidden,
    maxHeight(98 vh)
    )

  ".graph" -(
    borderColor(blue),
    borderWidth(3 px)
    )



  ".CodeMirror" -(
    height.auto important,
    minHeight(15.0 vh),
    maxHeight(100 %%),
    width(100 %%)
    //height(100.0 %%) important
    // width.auto important
    )

  ".CodeMirror-scroll" -(
    overflow.visible,
    height.auto
    )//-(overflowX.auto,overflowY.hidden)

  ".breakpoints" - (
    width( 3 em)
    )

  ".focused" - (
    backgroundColor.ghostwhite
    )

  "#Papers" -(
    padding(0 px)
    )

  ".ui.segment.paper" -(
    padding(0 px),
    minHeight(98.0 vh)
    )


  "#LeftGraph" -(
    padding(0 px)
    )

  "#RightGraph" -(
    padding(0 px)
    )

  ".project.content" -{
    cursor.pointer
  }
}


