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

object ChatEntities {

  sealed trait CirceSerializable extends Serializable

  sealed trait Entity extends CirceSerializable

  case class ChatNotification(users: Users, entity: Entity) extends CirceSerializable

  case class ErrorMessage(value: String) extends Entity

  sealed trait Status extends Entity
  case object Online extends Status
  case object Offline extends Status

  case class Date(value: String) extends Entity

  case class Message(dialogReference: DialogReference, userFullInfo: UserFullInfo, value: String, date: Date) extends Entity
  case class Messages(value: List[Message]) extends Entity

  object Messages {
    def empty: Messages = Messages(List())
  }

  case class UserProfile(email: String, name: String, picture: String, user_id: String, nickname: String, createdAt: String)
  case class UserReference(id: String) extends Entity
  case class UserInfo(name: String, avatarUrl: String) extends Entity
  case class Users(value: List[UserReference]) extends Entity
  case class UserFullInfo(userReference: UserReference, userInfo: UserInfo, status: Status)

  case class DialogReference(id: String) extends Entity
  case class DialogReferenceOpt(ref: Option[DialogReference]) extends Entity
  case class DialogInfo(users: Users) extends Entity
  case class Dialogs(value: List[DialogReference]) extends Entity


  sealed trait ChatCommand extends Entity
  sealed trait ChatEvent extends Entity

  case class AddUser(info: UserInfo) extends ChatCommand

  sealed trait UserAddingEvent extends ChatEvent  {val user: UserFullInfo}
  case class UserAdded(user: UserFullInfo) extends UserAddingEvent
  case class UserAlreadyExists(user: UserFullInfo) extends UserAddingEvent

  case class Login(reference: UserReference) extends ChatCommand
  case class LoggedIn(reference: UserReference) extends ChatEvent

  case class Logout(reference: UserReference) extends ChatCommand
  case class LoggedOut(reference: UserReference) extends ChatEvent

  case class Connect(reference: UserReference) extends ChatCommand

  case class ChangeUserStatus(reference: UserReference, status: Status) extends ChatCommand
  case class StatusChanged(reference: UserReference, status: Status) extends ChatEvent

  case class AddDialog(users: Users) extends ChatCommand
  case class GetDialog(users: Users) extends ChatCommand
  case class DialogAdded(dialogReference: DialogReference) extends ChatEvent

  case class AddMessage(message: Message) extends ChatCommand
  case class MessageAdded(message: Message) extends ChatEvent

  case class ListMessages(reference: DialogReference) extends ChatCommand
  case class ListDialogs(reference: UserReference) extends ChatCommand
  case class ListOnlineUsers() extends ChatCommand
  case class ListUsers() extends ChatCommand
  case class GetUserInfo(userReference: UserReference) extends ChatCommand
  case class GetDialogInfo(dialogReference: DialogReference) extends ChatCommand

//  sealed trait WebSocketMessage extends CirceSerializable
//
//  case class WebSocketRequest(id: String, value: CirceSerializable) extends WebSocketMessage
//  case class WebSocketResponseData(value: CirceSerializable) extends WebSocketMessage
//  case class WebSocketResponse(request: WebSocketRequest, data: WebSocketResponseData) extends WebSocketMessage

}
