/**
  * Copyright 2017, Alexander Ray (dev@alexray.me)
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  */

package actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import shared.entities.ChatEntities._
import shared.entities.ChatModel

import scala.util.{Failure, Success, Try}

object ChatModelActor {
  def props(model: ChatModel, manager: ActorRef) = Props(new ChatModelActor(model, manager))
}

class ChatModelActor (model: ChatModel, manager: ActorRef) extends Actor with ActorLogging {

  def processTryF[R](t: Try[R])(f: R => Unit): Unit = t match {
    case Success(value) => f(value)
    case Failure(e) =>
      log.error(e.getLocalizedMessage)
      sender ! ErrorMessage(e.getLocalizedMessage)
  }

  def processTry[R](t: Try[R]): Unit =
    processTryF[R](t){ m =>
      log.info(s"process message $m")
      sender ! m
    }

  override def receive: Receive = {
    case AddUser(info) =>
      processTryF(model.addUser(info)) { (response: _root_.shared.entities.ChatEntities.UserAddingEvent) =>
        self ! Login(response.user.userReference)
        sender ! response
      }

    case Login(ref) => processTryF(model.login(ref)) { (m: StatusChanged) =>
      manager ! ChatNotification(model.listUsers().get, m)
    }

    case Logout(ref) => processTryF(model.logout(ref)) { (m: StatusChanged) =>
      manager ! ChatNotification(model.listUsers().get, m)
    }

    case AddDialog(users) => processTry(model.addDialog(users))
    case AddMessage(message) => processTryF(model.addMessage(message)) { (m: MessageAdded) =>
      model.getDialogInfo(message.dialogReference).foreach { info =>
        manager ! ChatNotification(info.users, m.message)
        sender ! m
      }
    }
    case ListDialogs(ref) => processTry(model.listDialogs(ref))
    case ListMessages(ref) => processTry(model.listDialogMessages(ref))
    case ListUsers() => processTry(model.listUsers())
    case ListOnlineUsers() => processTry(model.listOnlineUsers())
    case GetUserInfo(ref) => processTry(model.getUserInfo(ref))
    case GetDialogInfo(ref) => processTry(model.getDialogInfo(ref))
    case GetDialog(users) => processTry(model.getDialog(users))
  }
}
