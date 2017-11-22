/**
  * Copyright 2017, Alexander Ray (dev@alexray.me)
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  */

package chat.services

import com.greencatsoft.angularjs.core.{Log, RootScope}
import com.greencatsoft.angularjs.{Factory, Service, injectable}
import shared.entities.ChatEntities.{DialogReference, UserReference}

import scala.collection.immutable.Queue
import scala.scalajs.js.annotation.JSExportAll
import utils.BroadcastMessages._

import scala.scalajs.js

@JSExportAll
case class UserView(userReference: UserReference,
                    name: String, avatar: String, date: String, lastMessage: String) //, online: Boolean)

@JSExportAll
case class DialogView(dialogReference: DialogReference, name: String, avatar: String, message: String, date: String)

@JSExportAll
case class SettingView(ref: String, name: String, description: String)

@JSExportAll
case class MessageView(uref: String, uname: String, uavatar: String, message: String, date: String)

@injectable("modelService")
class ModelService(utilsService: UtilsService, log: Log) extends Service
{

  private var users = Map[UserReference, UserView]()
  private var dialogMessages = Map[DialogReference, Queue[MessageView]]().withDefaultValue(Queue())
  private var dialogs = Map[DialogReference, DialogView]()

  private var connected = false
  private var loggedIn = false


  def loadUsers(value: List[UserView]): Unit = {
    log.info(s"modelService - LoadUsers: $value")
    users = value.map(u => u.userReference -> u).toMap
    notifyUsersUpdated()
  }

  def updateUser(value: UserView): Unit = {
    users += value.userReference -> value
    notifyUsersUpdated()
  }

  def notifyUsersUpdated(): Unit = {
    utilsService.broadcastEvent(usersUpdatedEvent, users.values.toList)
  }



  def loadDialogs(value: List[DialogView]): Unit = {
    dialogs = value.map(d => d.dialogReference -> d).toMap
    notifyDialogsUpdated()
  }

  def updateDialog(value: DialogView): Unit = {
    dialogs += value.dialogReference -> value
    notifyDialogsUpdated()
  }

  def notifyDialogsUpdated(): Unit = {
    utilsService.broadcastEvent(dialogsUpdatedEvent, dialogs.values.toList)
  }



  def loadMessagesForDialog(dialogReference: DialogReference, messages: Queue[MessageView]): Unit = {
    dialogMessages += dialogReference -> messages
    utilsService.broadcastEvent(dialogMessagesLoadedEvent, dialogReference, messages)
  }

  def addMessageToDialog(dialogReference: DialogReference, message: MessageView): Unit = {
    dialogMessages += dialogReference -> (dialogMessages(dialogReference) :+ message)
    utilsService.broadcastEvent(dialogMessageAddedEvent, dialogReference, message)
  }

}

@injectable("modelService")
class ModelServiceFactory(utilsService: UtilsService, log: Log) extends Factory[ModelService] {
  override def apply(): ModelService = new ModelService(utilsService, log)
}
