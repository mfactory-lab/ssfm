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

import chat.services.{CommunicationService, UserView}
import com.greencatsoft.angularjs.core.{Log, Scope}
import com.greencatsoft.angularjs.{AbstractController, injectable}
import shared.entities.ChatEntities.{UserReference, Users}
import utils.BroadcastMessages._
import utils.Utils._

import scala.scalajs.js
import scala.scalajs.js.JSConverters._

@js.native
trait UsersScope extends Scope {

  var users: js.Array[UserView] = js.native
  var selected: UserReference = js.native

  var select: js.Function = js.native

}

@injectable("UsersController")
class UsersController(scope: UsersScope, service: CommunicationService, log: Log)
  extends AbstractController[UsersScope](scope)
{

  implicit private val sc = scope

  scope.users = js.Array()
  scope.select = selectUser

  service.handleScopeEvent(usersUpdatedEvent)(loadUsers)
  service.requestUsers()

  private def selectUser = (ref: UserReference) => {
    scope.selected = ref
    service.processDialogWithCurrentUser(Users(List(ref)))(service.fireInitializeDialog)
  }

  private def loadUsers(users: List[UserView]) = {
    scope.users = users.sortBy(_.name).toJSArray
    scope.users.headOption.foreach(user => selectUser(user.userReference))
  }

}
