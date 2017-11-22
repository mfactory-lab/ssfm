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
import com.greencatsoft.angularjs.core.{Log, Timeout}
import org.scalajs.dom.Element
import org.scalajs.dom.html.Html

@injectable("scrollBottom")
class ScrollBottomDirective($timeout: Timeout, log: Log) extends AttributeDirective {

  override def link(scope: ScopeType, elems: Seq[Element], attrs: Attributes): Unit = {
    elems.headOption.map(_.asInstanceOf[Html]).foreach { elem =>
      scope.$watchCollection(attrs("scrollBottom"), () => {
        $timeout { () =>
          val chat = Angular.element(elem)(0)
          chat.scrollTop = chat.scrollHeight
        }
      })
    }
  }

}