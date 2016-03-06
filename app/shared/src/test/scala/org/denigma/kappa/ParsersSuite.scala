package org.denigma.kappa

import org.denigma.kappa.notebook.parsers.CommentLinksParser
import org.scalatest.{Matchers, WordSpec}
import org.scalatest.concurrent.Futures

/**
  * Created by antonkulaga on 06/03/16.
  */
class ParsersSuite extends WordSpec with Matchers  {


  "Kappa parsers" should {
    "parse charts" in {

    }

    "parse links" in {

    }

    "parse agents" in {

    }

    "parse rules" in {

    }

    "parse comments" in {

      val parser = new CommentLinksParser


    }
  }
}

trait TestModel {

  val data =
    """
      |####### TEMPLATE MODEL AS DESCRIBED IN THE MANUAL#############
      |
      |#### Signatures
      |%agent: A(x,c) # Declaration of agent A
      |%agent: B(x) # Declaration of B
      |%agent: C(x1~u~p,x2~u~p) # Declaration of C with 2 modifiable sites
      |
      |#### Rules
      |'a.b' A(x),B(x) <-> A(x!1),B(x!1) @ 'on_rate','off_rate' #A binds B
      |'ab.c' A(x!_,c),C(x1~u) ->A(x!_,c!2),C(x1~u!2) @ 'on_rate' #AB binds C
      |'mod x1' C(x1~u!1),A(c!1) ->C(x1~p),A(c) @ 'mod_rate' #AB modifies x1
      |'a.c' A(x,c),C(x1~p,x2~u) -> A(x,c!1),C(x1~p,x2~u!1) @ 'on_rate' #A binds C on x2
      |'mod x2' A(x,c!1),C(x1~p,x2~u!1) -> A(x,c),C(x1~p,x2~p) @ 'mod_rate' #A modifies x2
      |
      |#### Variables
      |%var: 'on_rate' 1.0E-4 # per molecule per second
      |%var: 'off_rate' 0.1 # per second
      |%var: 'mod_rate' 1 # per second
      |%obs: 'AB' A(x!x.B)
      |%obs: 'Cuu' C(x1~u?,x2~u?)
      |%obs: 'Cpu' C(x1~p?,x2~u?)
      |%obs: 'Cpp' C(x1~p?,x2~p?)
      |
      |
      |#### Initial conditions
      |%init: 1000 A(),B()
      |%init: 10000 C()
      |
      |%mod: [true] do $FLUX "flux.html" [true]
      |%mod: [T]>20 do $FLUX "flux.html" [false]
      |%def: "relativeFluxMaps" "true"
    """.stripMargin

}