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

import chat.services.{CommunicationService, DialogView}
import com.greencatsoft.angularjs.core.{Log, RootScope, Scope}
import com.greencatsoft.angularjs.{AbstractController, injectable}
import shared.entities.ChatEntities.DialogReference
import utils.BroadcastMessages._
import utils.Utils._

import scala.scalajs.js.JSConverters._
import scala.scalajs.js

@js.native
trait DialogsScope extends Scope {
  var dialogs: js.Array[DialogView] = js.native

  var selectedDialog: DialogReference = js.native
  var selectDialog: js.Function = js.native
}

@injectable("DialogsController")
class DialogsController(scope: DialogsScope, service: CommunicationService, log: Log, rootScope: RootScope)
  extends AbstractController[DialogsScope](scope)
{

  implicit private val sc = scope

  private val selectDialogF = (ref: DialogReference) => {
    scope.selectedDialog = ref
    service.fireInitializeDialog(ref)
  }

  scope.selectDialog = selectDialogF
  service.handleScopeEvent(dialogsUpdatedEvent)(loadDialogs)

  private def loadDialogs(dialogs: List[DialogView]) = {
    dialogs.headOption.foreach { dialogView =>
      scope.selectedDialog = dialogView.dialogReference
      selectDialogF(dialogView.dialogReference)
    }
    scope.dialogs = dialogs.toJSArray
  }


}
