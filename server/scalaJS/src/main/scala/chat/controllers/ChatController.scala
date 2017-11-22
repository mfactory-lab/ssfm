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

import chat.services.{CommunicationService, MessageView}
import com.greencatsoft.angularjs.core.{Log, Scope}
import com.greencatsoft.angularjs.{AbstractController, injectable}
import shared.entities.ChatEntities._
import utils.BroadcastMessages.{initializeDialog, updateDialogMessages}
import utils.Utils._

import scala.language.postfixOps
import scala.scalajs.js
import scala.scalajs.js.JSConverters._

@js.native
trait ChatScope extends Scope {

  var messages: js.Array[MessageView] = js.native
  var message: String = js.native

  var connect: js.Function = js.native
  var send: js.Function = js.native

  var currentUserRef: String = js.native
  var currentDialog: String = js.native
  var currentUserName: String = js.native
  var participants: js.Array[String] = js.native
  var dialogName: String = js.native

  var streamConnected: Boolean = js.native

  var messageBelongsToCurrentUser: js.Function = js.native
}

@injectable("ChatController")
class ChatController (scope: ChatScope, service: CommunicationService, log: Log) extends AbstractController[ChatScope](scope) {

  implicit private val sc = scope

  scope.messageBelongsToCurrentUser = (messageView: MessageView) => messageBelongsToCurrentUser(messageView)
  scope.messages = js.Array[MessageView]()
  scope.message = ""
  scope.currentDialog = ""
  scope.currentUserRef = service.getUserReference().fold("")(_.id)
  scope.streamConnected = false
  scope.$on(initializeDialog, (event: js.Any, ref: js.Any) => initDialog(ref.asInstanceOf[DialogReference]))
  scope.$on(updateDialogMessages, (event: js.Any, mv: js.Any) => updateDialog(mv.asInstanceOf[MessageView]))
  scope.send = () => send()

  def send(): Unit = {
    service.sendMessage(scope.message, scope.currentDialog)
    scope.message = ""
  }

  def updateDialog(message: MessageView): Unit = {
    scope.$apply(scope.messages.push(message))
  }

  def initDialog(dialogReference: DialogReference): Unit = {
    if (scope.currentDialog != dialogReference.id) {
      scope.currentDialog = dialogReference.id

//      if (!scope.streamConnected) {
//        service.applyMessageToScope(scope.currentDialog) { message =>
//          scope.messages.push(message)
//        }
//      }

      service.listMessages(dialogReference).applyToScope { messages =>
        scope.messages = messages.toJSArray
      }

      service.getDialogView(dialogReference).applyToScope { dialog =>
        val currentUserName = service.getUserInfo().fold("")(_.userInfo.name)
        scope.currentUserName = currentUserName
        scope.participants = dialog.name.split(", ").toJSArray
        scope.dialogName = dialog.name
      }
    }
  }

  def messageBelongsToCurrentUser(messageView: MessageView): Boolean = {
    service.getUserReference().fold(false)(_.id == messageView.uref)
  }

}
