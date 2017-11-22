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

import com.greencatsoft.angularjs.core.{RootScope, Scope, Window}
import com.greencatsoft.angularjs.{Factory, Service, injectable}
import shared.entities.ChatEntities._
import utils.BroadcastMessages._
import wvlet.log.LogSupport
import scala.collection.immutable.Queue
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js

@injectable("communicationService")
class CommunicationService(rootScope: RootScope, window: Window,
                           modelService: ModelService, transportService: TransportService)
  extends Service with LogSupport
{

  //  private var userReference: Option[UserReference] = None
  private var userInfo: Option[UserFullInfo] = None
  private var loggedIn: Boolean = false
  private var dialogs = Map[DialogReference, Queue[MessageView]]().withDefaultValue(Queue())

  private val lastMessageLength = 20

  def getLoggedIn(): Boolean = loggedIn

  def getUserInfo(): Option[UserFullInfo] = userInfo


  def login(name: String, avatarUrl: String): Future[Boolean] = {
    transportService.ask(AddUser(UserInfo(name, avatarUrl))).mapTo[UserAddingEvent].collect {
      case UserAdded(info) => info
      case UserAlreadyExists(info) => info
    }.map { info: UserFullInfo =>
      userInfo = Some(info)
      transportService.send(Connect(info.userReference))
      setLoggedIn()
      watchOnNotifications()
      initializeModel()
      true
    }
  }

  private def setLoggedIn() = {
    if (!loggedIn) {
      rootScope.$broadcast(loggedInBroadcast)
      loggedIn = true
    }
  }

  def logout(): Unit = {
    userInfo.foreach(userInfo => transportService.send(Logout(userInfo.userReference)))
    window.location.href = "/auth0logout"
  }

  def listUsers(): Future[List[UserView]] = {

//    transportService.ask(ListUsers()).mapTo[Users]
//      .flatMap{ userReferences =>
//
//        info(s"listUsers(): $userReferences")
//
//        Future.sequence(userReferences.value.map(getUserView))
//      }

    userInfo.fold(Future(List[UserView]())) { info =>
      transportService.ask(ListUsers()).mapTo[Users]
        .flatMap(userReferences => Future.sequence(userReferences.value.filterNot(_.id == info.userReference.id).map(getUserView)))
    }
  }

  def getUserView(userReference: UserReference): Future[UserView] = {
    transportService.ask(GetUserInfo(userReference)).mapTo[UserInfo].map { info =>
      UserView(userReference, info.name, info.avatarUrl, "", "Last seen 31.03.2017, 12:59:07")
    }
  }

  def listDialogs(): Future[List[DialogView]] = {
    userInfo.fold(Future(List[DialogView]())) { info =>
      transportService.ask(ListDialogs(info.userReference)).mapTo[Dialogs]
        .flatMap(dialogReferences => Future.sequence(dialogReferences.value.map(getDialogView)))
    }

  }

  def getDialogView(dialogReference: DialogReference): Future[DialogView] = {
    for {
      info <- transportService.ask(GetDialogInfo(dialogReference)).mapTo[DialogInfo]
      users <- Future.sequence(info.users.value.map(getUserView))
      messages <- listMessages(dialogReference)
    } yield {

      val lastMessageView = messages.sortBy(_.date).reverse.headOption.getOrElse(MessageView("", "", "", "", ""))
      val lastMessage =
        if (lastMessageView.message.length > lastMessageLength - 1) {
          lastMessageView.message.substring(0, lastMessageLength) + "..."
        } else {
          lastMessageView.message
        }

      val userRefs = users.map(_.userReference.id)

      val name = userInfo.map { info =>
        if (userRefs.count(_ == info.userReference.id) > 1) {
          users.distinct.map(_.name).mkString(", ")
        } else {
          users.filterNot(_.userReference.id == info.userReference.id).map(_.name).mkString(", ")
        }
      }.getOrElse("")

      val avatar = userInfo.map { info =>
        if (userRefs.count(_ == info.userReference.id) > 1) {
          users.head.avatar
        } else {
          users.filterNot(_.userReference.id == info.userReference.id).head.avatar
        }
      }.getOrElse("")

      DialogView(dialogReference, name, avatar, lastMessage, lastMessageView.date)
    }
  }

  def listMessages(ref: DialogReference): Future[List[MessageView]] = {
    transportService.ask(ListMessages(ref)).mapTo[Messages].map {
      _.value.map { message =>
        MessageView(
          message.userFullInfo.userReference.id,
          message.userFullInfo.userInfo.name,
          message.userFullInfo.userInfo.avatarUrl,
          message.value,
          message.date.value
        )
      }
    }
  }

  def sendMessage(message: String, dialogReference: String): Unit = {
    userInfo.foreach { info =>
      val m = AddMessage(Message(DialogReference(dialogReference), info, message, Date("")))
      transportService.send(m)
    }
  }

  def applyMessageToScope(dialogReference: String)(f: MessageView => Unit)(implicit sc: Scope): Unit = {
    info(s"watching for dialog messages $dialogReference")
    transportService.onMessage {
      case ChatNotification(users: Users, notification: CirceSerializable)
        if userInfo.isDefined && users.value.contains(userInfo.get.userReference) =>
        notification match {
          case message: Message if message.dialogReference.id == dialogReference =>
            sc.$apply(f {
              MessageView(
                message.userFullInfo.userReference.id,
                message.userFullInfo.userInfo.name,
                message.userFullInfo.userInfo.avatarUrl,
                message.value,
                message.date.value
              )
            })
          case message: Message =>
            warn(s"dialogReference match error '$dialogReference' != '${message.dialogReference.id}'")
          case other =>
            warn(s"unknown notification $other")

        }
      case msg => warn(s"unknown message $msg")
    }
  }

  def processDialogWithCurrentUser(users: Users)(f: DialogReference => Unit): Unit = {
    getOrCreateDialog(Users((getUserReference().get :: users.value).sortBy(_.id))).foreach(f)
  }

  def getUserReference(): Option[UserReference] = userInfo.map(_.userReference)

  def getOrCreateDialog(users: Users): Future[DialogReference] = {
    transportService.ask(GetDialog(users)).mapTo[DialogReferenceOpt].flatMap { optRef =>
      optRef.ref.fold {
        transportService.ask(AddDialog(users)).mapTo[DialogReference]
      } { x => Future(x) }
    }
  }

  def fireInitializeDialog(dialogReference: DialogReference): Unit = {
    rootScope.$broadcast(initializeDialog, dialogReference.asInstanceOf[js.Any])
  }

  private def watchOnNotifications(): Unit = {
    transportService.onMessage {
      case ChatNotification(users: Users, notification: CirceSerializable)
        if userInfo.isDefined && users.value.contains(userInfo.get.userReference) =>
        notification match {
          case message: Message =>
            val messageView = MessageView(
              message.userFullInfo.userReference.id,
              message.userFullInfo.userInfo.name,
              message.userFullInfo.userInfo.avatarUrl,
              message.value,
              message.date.value
            )
//            dialogs += message.dialogReference -> (dialogs(message.dialogReference) :+ messageView)
            rootScope.$broadcast(updateDialogMessages, messageView.asInstanceOf[js.Any])
            info(s"broadcast $messageView")
          case statusChanged: StatusChanged =>
          case other =>
            warn(s"unknown notification $other")

        }
      case msg => warn(s"unknown message $msg")
    }
  }

  def emitEvent(eventName: String, args: Any*)(implicit scope: Scope): Unit = {
    scope.$emit(eventName, args.map(_.asInstanceOf[js.Any]))
  }

  def initializeModel(): Unit = {
    listUsers().foreach(modelService.loadUsers)
    listDialogs().foreach(modelService.loadDialogs)
  }

  def broadcastEventHandler[T](handler: T => Unit): js.Function = {
    val f = (event: js.Any, data: js.Any) => {
      handler(data.asInstanceOf[T])
    }
    f
  }

  def handleScopeEvent[T](event: String)(handler: T => Unit)(implicit scope: Scope) = {
    scope.$on(event, broadcastEventHandler[T](handler))
  }

  def requestUsers() = modelService.notifyUsersUpdated()

}

@injectable("communicationService")
class CommunicationServiceFactory(rootScope: RootScope, window: Window,
                                  modelService: ModelService, transportService: TransportService)
  extends Factory[Service]
{
  override def apply(): Service = new CommunicationService(rootScope, window, modelService, transportService)
}
