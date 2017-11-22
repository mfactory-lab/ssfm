/**
  * Copyright 2017, Alexander Ray (dev@alexray.me)
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  */

package controllers.chat

import javax.inject.{Inject, Singleton}

import actors.ChatManager
import actors.ChatManager._
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.pattern.{ask, pipe}
import akka.stream.Materializer
import akka.util.Timeout
import io.circe.generic.auto._
import play.Logger
import play.api.libs.json.JsValue
import play.api.libs.streams.ActorFlow
import play.api.mvc._
import shared.entities.ChatEntities._
import shared.utils.CirceTransportSerializers._
import shared.utils.{TransportDecoder, TransportEncoder}
import shared.Common._

import scala.concurrent.duration._
import scala.language.postfixOps

@Singleton
class ChatController @Inject() (implicit system: ActorSystem, materializer: Materializer) extends Controller {


  private val encoder = implicitly[TransportEncoder[CirceSerializable, PlainMessage]]
  private val decoder = implicitly[TransportDecoder[CirceSerializable, PlainMessage]]

  private val transportEncoder = implicitly[TransportEncoder[TransportMessage, String]]
  private val transportDecoder = implicitly[TransportDecoder[TransportMessage, String]]

  Logger.info("creating chat manager")

  private val chatManager = system.actorOf(ChatManager.props(), "manager")
  private var userReference: Option[UserReference] = None
  private var userProfile: Option[UserProfile] = None

  private def decode[D, T](value: D)(f: T => Unit)(implicit decoder: TransportDecoder[T, D]): Unit = {
    decoder.decode(value) match {
      case Left(e) => Logger.error(s"decoding error: '$e' - '$value'")
      case Right(v) => f(v)
    }
  }

  class ChatConnection(sender: ActorRef, manager: ActorRef) extends Actor with ActorLogging
  {
    implicit private val ec = context.dispatcher
    implicit val timeout = Timeout(5 minutes)

    manager ! AddChatClient(self)

    override def receive: Receive = {
      case Connect(ref) => userReference = Some(ref)
      case command: ChatCommand =>
        log.info(command.toString)
        manager ! command
      case notification @ ChatNotification(users, _) =>
        userReference.foreach { ref =>
          if (users.value.contains(ref)) {
            val msg = transportEncoder.encode(encoder.encode(notification))
            sender ! msg
          }
        }
      case request @ RequestMessage(uid, data: PlainMessage) =>
        log.info(s"received RequestMessage '$request'")
        decode(data) { message: CirceSerializable =>
          (manager ? message)
            .mapTo[CirceSerializable]
            .map{ (rd: CirceSerializable) =>
              log.info(s"response: $rd")
              transportEncoder.encode(ResponseMessage(uid, encoder.encode(rd)))
            } pipeTo sender
        }

      case request @ RequestMessage =>
        log.error(s"wrong request: $request")

      case msg @ PlainMessage(data) =>
        log.info(s"received PlainMessage '$data'")
        decode[PlainMessage, CirceSerializable](msg){ decodedMsg =>
          log.info(s"decoded PlainMessage '$decodedMsg'")
          self ! decodedMsg
        }

      case c: CirceSerializable =>
        manager ! c

      case msg: String =>
        log.info(s"received message '$msg'")
        decode[String, TransportMessage](msg)(self ! _)(transportDecoder)

      case msg =>
        log.warning(s"unknown message '$msg'")

    }

    override def postStop(): Unit = {
      manager ! RemoveChatClient(self)
    }

  }

  def chatSocket: WebSocket = WebSocket.accept[String, String] { _ =>
    ActorFlow.actorRef{ sender =>
      Props(new ChatConnection(sender, chatManager))
    }
  }

  def onLogout(): Unit =  userReference.foreach(chatManager ! Logout(_))

  def setUserProfile(profile: JsValue): Unit = {
    // TODO: save as UserProfile
//    userProfile = Some(UserProfile("..."))
    Logger.info(profile.toString)
  }

}
