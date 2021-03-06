package org.denigma.kappa

import fastparse.core.Parsed
import org.denigma.kappa.notebook.parsers.{AST, CommentLinksParser, FilesParser, KappaParser}
import org.scalatest.{Inside, Matchers, WordSpec}

class  FilesParserSuite extends WordSpec with Matchers with Inside  {
  "FilesParser" should {
    "parse sources" in {
      val parser = new FilesParser
      val linenum = ":on_line 150"
      inside(parser.onLine.parse(linenum)) {
        case Parsed.Success(150, _) =>
      }
      val wrongLine = ":in_source"
      inside(parser.source.parse(wrongLine)){
        case f: Parsed.Failure =>
      }
      val linePrefixed = ":in_source :DNA_REPAIR/figure.jpg"
      inside(parser.source.parse(linePrefixed))
      {
        case Parsed.Success((AST.IRI(":DNA_REPAIR/figure.jpg"), None), _) =>
      }
      val lineURL = ":in_source <http://helloworld.com>"
      inside(parser.source.parse(lineURL))
      {
        case Parsed.Success((AST.IRI("http://helloworld.com"), None), _) =>
      }

      val place = s":in_source :DNA_REPAIR/figure.jpg ; $linenum"
      inside(parser.source.parse(place)){
        case Parsed.Success((AST.IRI(":DNA_REPAIR/figure.jpg"), Some(150)), _) =>
      }
    }

    "parse figures" in {
      val parser = new FilesParser


      val ln0 =" :image :dna_repair_tutorial/plasmid.png"
      inside(parser.image.parse(ln0)) {
        case Parsed.Success(AST.IRI(":dna_repair_tutorial/plasmid.png"), _) =>

      }

      println("PARSE FIG: "+parser.image.parse(ln0))

      val ln = ":image :dna_repair_tutorial/kappa_biobrick.jpg"
      inside(parser.image.parse(ln)) {
        case Parsed.Success(AST.IRI(":dna_repair_tutorial/kappa_biobrick.jpg"), _) =>
      }

      val video = ":video <https://www.youtube.com/watch?v=HFwSnqYC5LA>"
      inside(parser.video.parse(video)){
        case Parsed.Success(AST.IRI("https://www.youtube.com/watch?v=HFwSnqYC5LA"), _) =>
      }

      val ln2 = ":image :dna_repair_tutorial/kappa_biobrick.jpg \n"
      inside(parser.image.parse(ln2)) {
        case Parsed.Success(AST.IRI(":dna_repair_tutorial/kappa_biobrick.jpg"), _) =>
      }
    }
  }


}
