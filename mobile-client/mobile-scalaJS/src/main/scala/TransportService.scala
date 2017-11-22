/**
  * Copyright 2017, Alexander Ray (dev@alexray.me)
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  */

import shared.entities.ChatEntities._
import shared.entities.FacadeTypes._
import shared.utils.CirceTransportSerializers._
import utils.Utils._
import utils.mediator.Communicator
import utils.mediator.transport._
import wvlet.log._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.Promise
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

case class CommunicatorAdapter(socket: SocketIO) extends LogSupport {

  private val communicator = Communicator(SocketIOTransportFactory(SocketIOConfig(socket)))

  def send[T <: CirceSerializable](value: T): Unit = {
    communicator.send(value) //(composedEncoder)
  }

  def onMessage(user: JsContactReference, subject: JsSubject): Unit = {
    communicator.onDecodedMessage[CirceSerializable] {
      case ChatNotification(users: Users, entity: Entity) =>
        users.value.find(_.id == user.id).map(_ => {
          entity match {
            case Message(_dialogReference: DialogReference, _userFullInfo: UserFullInfo, _value: String, _date: Date) =>

              val _contactReference: JsContactReference = new JsContactReference {
                val id: String = _userFullInfo.userReference.id
              }

              val _contactInfo: JsContactInfo = new JsContactInfo {
                val contactReference: JsContactReference = _contactReference
                val name: String = _userFullInfo.userInfo.name
                val avatar: String = _userFullInfo.userInfo.avatarUrl
              }

              val reference: JsDialogReference = new JsDialogReference {
                override val id: String = _dialogReference.id
              }

              val jsMessage: JsMessage = new JsMessage {
                val date: String = _date.value
                val contactInfo: JsContactInfo = _contactInfo
                val dialogReference: JsDialogReference = reference
                val value: String = _value
              }

              subject.next(jsMessage)
            case _ =>
          }
        })
      case x => warn(s"required ChatNotification, but received $x")
    }
  }

  def onStatusChanged(subject: JsSubject): Unit = {
    communicator.onDecodedMessage[CirceSerializable] {
      case ChatNotification(users: Users, entity: Entity) =>
        entity match {
          case StatusChanged(reference: UserReference, status: Status) =>
            status match {
              case Online =>
                getJsContact(reference).map(contactInfo => {
                  subject.next(new JsStatusChangedEvent {
                    val contact: JsContactInfo = contactInfo
                    val online: Boolean = true
                  })
                })
              case Offline =>
                getJsContact(reference).map(contactInfo => {
                  subject.next(new JsStatusChangedEvent {
                    val contact: JsContactInfo = contactInfo
                    val online: Boolean = false
                  })
                })
              case _ =>
            }
          case _ =>
        }
      case x => warn(s"required ChatNotification, but received $x")
    }
  }

  def getJsDialog(reference: DialogReference): Future[JsDialogInfo] = {
    for {
      info <- ask(GetDialogInfo(reference)).mapTo[DialogInfo]
      usersList <- Future.sequence(info.users.value.map(getJsContact))
      messages <- getMessages(reference)
    } yield {

      val _dialogReference = new JsDialogReference {
        val id: String = reference.id
      }

      val _users = usersList.toJSArray

      val _lastMessage = messages.value.sortBy(_.date.value).reverse.headOption.getOrElse {
        Message(DialogReference(""), UserFullInfo(UserReference(""), UserInfo("", ""), Offline), "", Date(""))
      }

      new JsDialogInfo {
        val dialogReference: JsDialogReference = _dialogReference
        val users: js.Array[JsContactInfo] = _users
        val lastMessage: String = _lastMessage.value
        val lastMessageDate: String = _lastMessage.date.value
      }
    }
  }

  def getMessages(ref: DialogReference): Future[Messages] =
    ask(ListMessages(ref)).mapTo[Messages]

  def getJsContact(userReference: UserReference): Future[JsContactInfo] = {
    ask(GetUserInfo(userReference)).mapTo[UserInfo].map { (info: UserInfo) =>
      new JsContactInfo {
        val contactReference: JsContactReference = new JsContactReference {
          val id: String = userReference.id
        }
        val name: String = info.name
        val avatar: String = info.avatarUrl
      }
    }
  }

  def ask[T <: CirceSerializable](value: T): Future[CirceSerializable] = {
    communicator.?[CirceSerializable, CirceSerializable](value)
  }

  def getJsMessage(message: Message): JsMessage = {

    val _dialogReference: JsDialogReference = new JsDialogReference {
      val id: String = message.dialogReference.id
    }

    val _contactInfo: JsContactInfo = new JsContactInfo {
      val contactReference: JsContactReference = new JsContactReference {
        val id: String = message.userFullInfo.userReference.id
      }
      val name: String = message.userFullInfo.userInfo.name
      val avatar: String = message.userFullInfo.userInfo.avatarUrl
    }

    val jsMessage: JsMessage = new JsMessage {
      val dialogReference: JsDialogReference = _dialogReference
      val contactInfo: JsContactInfo = _contactInfo
      val value: String = message.value
      val date: String = message.date.value
    }

    jsMessage
  }

}

@JSExportTopLevel("TransportService") // This name will be available in TypeScript
@JSExportAll
class TransportService(socket: SocketIO) extends LogSupport {

  Logger.setDefaultFormatter(LogFormatter.SourceCodeLogFormatter)
  Logger.setDefaultHandler(JSConsoleLogHandler())

  Logger.setDefaultLogLevel(LogLevel.DEBUG)

  val adapter = CommunicatorAdapter(socket)

  def addUser(userInfo: JsUserInfo): js.Promise[JsContactInfo] = {
    adapter.ask(AddUser(UserInfo(userInfo.name, userInfo.avatarUrl)))
      .mapTo[UserAddingEvent]
      .collect {
        case UserAdded(info) => info
        case UserAlreadyExists(info) => info
      }
      .map { info: UserFullInfo =>
        new JsContactInfo {
          val contactReference: JsContactReference = new JsContactReference {
            val id: String = info.userReference.id
          }
          val name: String = info.userInfo.name
          val avatar: String = info.userInfo.avatarUrl
        }
      }
      .toDefaultPromise
  }

  def sendConnect(reference: JsContactReference): Unit =
    adapter.send(Connect(UserReference(reference.id)))

  def listenNewMessages(user: JsContactReference, subject: JsSubject): Unit = adapter.onMessage(user, subject)

  def listenNewUsers(subject: JsSubject): Unit = adapter.onStatusChanged(subject)

  def listContacts(): Promise[js.Array[JsContactInfo]] = {
    adapter.ask(ListOnlineUsers())
      .mapTo[Users]
      .flatMap(userReferences => Future.sequence(userReferences.value.map(adapter.getJsContact)))
      .map(_.toJSArray)
      .toDefaultPromise
  }

  def listDialogs(reference: JsContactReference): Promise[js.Array[JsDialogInfo]] = {
    adapter.ask(ListDialogs(UserReference(reference.id)))
      .mapTo[Dialogs]
      .flatMap(dialogReferences => Future.sequence(dialogReferences.value.map(adapter.getJsDialog)))
      .map(_.toJSArray)
      .toDefaultPromise
  }

  def getOrCreateDialog(user1: JsContactReference, user2: JsContactReference): Promise[JsDialogInfo] = {
    val users = Users(List(UserReference(user1.id), UserReference(user2.id)))
    adapter.ask(GetDialog(users))
      .mapTo[DialogReferenceOpt]
      .flatMap { optRef =>
        optRef.ref.foreach(ref => debug(s"Dialog already exist: $ref, users: (${user1.id}, ${user2.id})"))
        optRef.ref.fold {
          adapter.ask(AddDialog(users)).mapTo[DialogReference].map(ref => {
            debug(s"Created new dialog: $ref, users: (${user1.id}, ${user2.id})")
            ref
          })
        } { x => Future(x) }
      }
      .flatMap(adapter.getJsDialog)
      .toDefaultPromise
  }

  def getDialogInfo(reference: JsDialogReference): Promise[JsDialogInfo] =
    adapter.getJsDialog(DialogReference(reference.id))
      .toDefaultPromise

  def listMessages(reference: JsDialogReference): Promise[js.Array[JsMessage]] =
    adapter.getMessages(DialogReference(reference.id))
      .map(messages => messages.value.map(adapter.getJsMessage).toJSArray)
      .toDefaultPromise

  def sendMessage(message: JsMessage): Unit = {
    val info = UserFullInfo(UserReference(message.contactInfo.contactReference.id), UserInfo(message.contactInfo.name, message.contactInfo.avatar), Online)
    val m = AddMessage(Message(DialogReference(message.dialogReference.id), info, message.value, Date("")))
    adapter.send(m)
  }

  def logout(contactInfo: JsContactInfo): Unit = {
    adapter.send(Logout(UserReference(contactInfo.contactReference.id)))
  }

}
