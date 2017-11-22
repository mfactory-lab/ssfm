/**
  * Copyright 2017, Alexander Ray (dev@alexray.me)
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  */

package chat.directives

import com.greencatsoft.angularjs._
import com.greencatsoft.angularjs.core.Log
import org.scalajs.dom.html.Html
import org.scalajs.dom.{Element, KeyboardEvent}
import org.scalajs.dom.document
import org.scalajs.dom.raw.NodeList

@injectable("tabFocus")
class TabFocusDirective(log: Log) extends AttributeDirective {

  var tabIndex: Int = 0
  val tabs: NodeList = document.querySelectorAll("div[tab-focus]")

  override def link(scope: ScopeType, elems: Seq[Element], attrs: Attributes) {

    def selectNext(index: Int): Unit = {
      tabIndex = if (index >= tabs.length - 1) 0 else index + 1
      tabs.item(tabIndex).asInstanceOf[Html].focus()
    }

    elems.map(_.asInstanceOf[Html]).foreach { elem =>
      elem.onkeydown = (event: KeyboardEvent) => {
        if (event.keyCode == 9) { // Tab
          event.stopPropagation()
          event.preventDefault()
          selectNext(tabIndex)
        }
      }
    }
  }

}