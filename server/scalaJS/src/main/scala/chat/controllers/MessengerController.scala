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
import com.greencatsoft.angularjs.core.Scope
import com.greencatsoft.angularjs.{AbstractController, injectable}
import shared.entities.ChatEntities.{Offline, UserFullInfo, UserInfo, UserReference}
import utils.BroadcastMessages._

import scala.scalajs.js

@js.native
trait MessengerScope extends Scope {

  var selected: Int = js.native
  var select: js.Function = js.native
  var userLogged: Boolean = js.native
  var currentUserName: String = js.native
}

@injectable("MessengerController")
class MessengerController(scope: MessengerScope, service: CommunicationService) extends AbstractController[MessengerScope](scope){

  scope.userLogged = false
  scope.$on(loggedInBroadcast, () => {
    scope.userLogged = true
    scope.currentUserName =
      service.getUserInfo().getOrElse(UserFullInfo(UserReference(""), UserInfo("", ""), Offline)).userInfo.name
  })

  scope.selected = 0
  scope.select = (i: Int) => {
    scope.selected = i
  }

}
