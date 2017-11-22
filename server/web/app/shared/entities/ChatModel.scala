/**
  * Copyright 2017, Alexander Ray (dev@alexray.me)
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  **/

package shared.entities

import java.text.SimpleDateFormat

import shared.entities.ChatEntities._

import scala.util.Try

trait ChatModel {

  private var registered = Map[UserReference, UserInfo]()
  private var byStatus = Map[Status, Set[UserReference]](Online -> Set(), Offline -> Set())

  private var dialogInfos = Map[DialogReference, DialogInfo]()
  private var dialogs = Map[DialogReference, Messages]()

  def uidGenerator(): String = java.util.UUID.randomUUID.toString

  def getUserStatus(userReference: UserReference): Status = {
    // TODO: implement method
    Offline
  }

  def addUser(info: UserInfo): Try[UserAddingEvent] = Try {
      registered.filter(_._2 == info).toList.headOption match {
        case Some((userReference, userInfo)) =>
          UserAlreadyExists(UserFullInfo(userReference, userInfo, getUserStatus(userReference)))
        case None =>
          val ref = UserReference(uidGenerator())
          registered += (ref -> info)
          UserAdded(UserFullInfo(ref, info, getUserStatus(ref)))
      }
  }


  def changeStatus(userReference: UserReference, status: Status): Try[StatusChanged] = Try {
    byStatus = byStatus.mapValues(_ - userReference) + (status -> (byStatus(status) + userReference))
    StatusChanged(userReference, status)
  }

  def login(userReference: UserReference): Try[StatusChanged] = changeStatus(userReference, Online)
  def logout(userReference: UserReference): Try[StatusChanged] = changeStatus(userReference, Offline)

  def addDialog(users: Users): Try[DialogReference] = Try {
    val ref = DialogReference(uidGenerator())

    dialogInfos += ref -> DialogInfo(users)
    dialogs += ref -> Messages.empty

    ref
  }

  def addMessage(message: Message): Try[MessageAdded] = Try {
    val format = new SimpleDateFormat("dd.MM.yyyy'\n'HH:mm:ss.SSS")
    val date = format.format(new java.util.Date())
    val newMessage = Message(message.dialogReference, message.userFullInfo, message.value, Date(date))
    dialogs += newMessage.dialogReference -> Messages(dialogs(newMessage.dialogReference).value :+ newMessage)
    MessageAdded(newMessage)
  }

  def listOnlineUsers(): Try[Users] = Try(Users(byStatus(Online).toList))
  def listUsers(): Try[Users] = Try(Users(registered.keys.toList))

  def listDialogs(userReference: UserReference): Try[Dialogs] = Try {
    Dialogs(dialogInfos.filter(_._2.users.value.contains(userReference)).keys.toList)
  }

  def listDialogMessages(dialogReference: DialogReference): Try[Messages] =
    Try(dialogs(dialogReference))

  def getUserInfo(userReference: UserReference): Try[UserInfo] = Try {
    registered(userReference)
  }

  def getDialogInfo(dialogReference: DialogReference): Try[DialogInfo] = Try {
    dialogInfos(dialogReference)
  }

  def getDialog(users: Users): Try[DialogReferenceOpt] =  Try {
    DialogReferenceOpt(
      dialogInfos.filter(tuple =>
        tuple._2.users.value.intersect(users.value) == users.value || tuple._2.users.value.intersect(users.value) == tuple._2.users.value
      ).keys.toList.headOption
    )
  }

  def getOrCreateDialog(users: Users): Try[DialogReference] =
    getDialog(users).flatMap(_.ref.fold {addDialog(users)} { x => Try(x)})

}

object ChatModel {
  def apply(): ChatModel = new ChatModel(){}
  def testModel(): ChatModel = {
    val m = new ChatModel(){}
    (1 to 3).foreach( i => m.addUser(UserInfo(s"TestUser-$i", "")))

    for {
      urefs <- m.registered.keys.sliding(2)
      users = Users(urefs.toList)
      dref <- m.addDialog(users)
      idx <- 1 to 3
    } {
//      m.addMessage(Message(dref, urefs.head, s"message - $idx", Date("Thu, 1 Jan 2000 00:00:00 GMT")))
      users.value.foreach(m.login)
    }

    m
  }
}
