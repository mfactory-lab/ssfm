/**
  * Copyright 2017, Alexander Ray (dev@alexray.me)
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  **/

package actors

import actors.ChatManager.{AddChatClient, RemoveChatClient}
import akka.actor.{Actor, ActorLogging, ActorRef, Props, Stash}
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import shared.entities.ChatEntities._
import shared.entities.ChatModel

import scala.concurrent.duration._
import scala.language.postfixOps

object ChatManager {
  def props() = Props(new ChatManager())

  case class AddChatClient(client: ActorRef)
  case class RemoveChatClient(client: ActorRef)
}

class ChatManager extends Actor with ActorLogging with Stash{

  implicit private var ec = context.dispatcher
  implicit val timeout = Timeout(5 minutes)

  private val model = ChatModel()
  private val modelActor = context.actorOf(ChatModelActor.props(model, self), "chatModelActor")

  private var chatClients = Set[ActorRef]()

  override def receive: Receive = {
    case command: ChatCommand => (modelActor ? command) pipeTo sender
    case notification @ ChatNotification(users, _) => chatClients.foreach(_ ! notification)
    case AddChatClient(client) => chatClients += client
    case RemoveChatClient(client) => chatClients -= client
  }
}
