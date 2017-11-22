/**
  * Copyright 2016, Alexander Ray (dev@alexray.me)
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  **/

package visualize.controllers

import com.greencatsoft.angularjs.core.Scope
import com.greencatsoft.angularjs.{AbstractController, injectable}
import org.scalajs.dom._

import scala.scalajs.js

@js.native
trait MainScope extends Scope {

  var message: String = js.native

}

@injectable("MainController")
class MainController(scope: MainScope)
  extends AbstractController[MainScope](scope) {

  console.info("MainController - initialized")

  scope.message = "ssfm started"

}
