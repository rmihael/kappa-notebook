package org.denigma.kappa.notebook.styles

import org.denigma.controls.papers.MediaQueries

import scalacss.Defaults._

object MyStyles extends MainStyles
  with TextLayerStyles
  with TabGridsStyles
  with ListStyles
  with CodeStyles
{
  import dsl._

  ".fileitem" -{
    fontSize(0.9 em)
  }

  ".plot" -(
      maxWidth(600 px),
      maxHeight(65 vh)
    )

  ".noscroll" -(
    overflowX.hidden important,
    overflowY.hidden important
    )

  "#runner" -(
    width(100 %%)
    )

  ".stack" -(
    display.inlineFlex,
    flexDirection.row,
    flexWrap.nowrap,
    alignContent.stretch
    )

  ".ui.table td.collapsing" -(
    padding(0 px) important
    )

  ".graph.container" -(
    minHeight(300 px),
    minWidth(300 px)
    )
  ".graph.container:before" -(
      content := "",
      display.block,
      paddingTop(100 %%)
    )
}



