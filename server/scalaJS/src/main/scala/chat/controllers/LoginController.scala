/**
  * Copyright 2017, Alexander Ray (dev@alexray.me)
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  */

package chat.controllers

import chat.services.{CommunicationService}
import com.greencatsoft.angularjs.core.{Log, Scope}
import com.greencatsoft.angularjs.{AbstractController, injectable}
import utils.Utils._

import scala.scalajs.js

@js.native
trait LoginScope extends Scope {
  var loggedIn: Boolean = js.native
  var profile: js.Object = js.native
  var name: String = js.native
  var avatarUrl: String = js.native
  var login: js.Function = js.native
}

@injectable("LoginController")
class LoginController(scope: LoginScope, service: CommunicationService, log: Log)
  extends AbstractController[LoginScope](scope)
{

  implicit private val sc = scope

  scope.loggedIn = false
  scope.name = ""
  scope.avatarUrl = ""
  scope.profile = js.Object

  scope.login = () => service.login(scope.name, scope.avatarUrl).applyToScope(scope.loggedIn = _)

}
