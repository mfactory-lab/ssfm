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

import com.greencatsoft.angularjs.{Factory, Service, injectable}
import io.circe.Decoder
import shared.entities.ChatEntities.CirceSerializable
import utils.mediator.Communicator
import utils.mediator.transport.{WebSocketConfig, WebSocketTransportFactory}
import wvlet.log.LogSupport

import scala.concurrent.Future
import shared.utils.CirceTransportSerializers._
import io.circe.generic.auto._
import shared.Common.{PlainMessage, TransportMessage}
import shared.utils.{DecodeError, MessageTransformer, TransportDecoder, TransportEncoder}

import scala.scalajs.js

@injectable("transportService")
class TransportService extends Service with LogSupport {

  private val communicator =
    Communicator(WebSocketTransportFactory(WebSocketConfig(path = "/api/ws/chat")))


  def ask[T <: CirceSerializable](value: T): Future[CirceSerializable] = {
    communicator.?[CirceSerializable, CirceSerializable](value)
  }

  def send[T <: CirceSerializable](value: T): Unit = {
    communicator.send(value)//(composedEncoder)
  }

  def onMessage(f: CirceSerializable => Unit): Unit = {
    info("def onMessage(f: CirceSerializable => Unit): Unit = {")
    communicator.onDecodedMessage[CirceSerializable]{ x =>
      info(s"communicator.onDecodedMessage[CirceSerializable]{ x => '$x'")
      f(x)
    }

  }


}

@injectable("transportService")
class TransportServiceFactory extends Factory[TransportService]
{
  override def apply(): TransportService = new TransportService()
}

