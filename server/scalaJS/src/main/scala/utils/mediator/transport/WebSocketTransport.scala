/**
  * Copyright 2017, Alexander Ray (dev@alexray.me)
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  */

package utils.mediator.transport

import org.scalajs.dom
import org.scalajs.dom.raw.{ErrorEvent, Event, MessageEvent, WebSocket}
import shared.utils.TransportEncoder

import scala.scalajs.js.JSON

case class WebSocketConfig(port: Int = 0, path: String = "") {
  def url: String = {
    val wsProtocol = if (dom.document.location.protocol == "https:") "wss" else "ws"
    s"$wsProtocol://${dom.document.location.hostname}:${if (port != 0) port.toString else dom.document.location.port}$path"
  }
}

trait WebSocketTransportFactory extends TransportFactory {

  def config: WebSocketConfig

  override def createTransport(): Transport = WebSocketTransport(config)
}

object WebSocketTransportFactory {
  def apply(c: WebSocketConfig): WebSocketTransportFactory = new WebSocketTransportFactory {
    override def config: WebSocketConfig = c
  }
}

trait WebSocketTransport extends Transport {

  type Config = WebSocketConfig
  type TransportEvent = Event
  type TransportError = ErrorEvent
  type TransportReceiveMessage = MessageEvent
  type TransportSendMessage = String

  private var socket: Option[WebSocket] = None
  val timeout = 10000 // ms
  val tryReconnect = false

  def internalSend(data: String): Unit = socket.foreach(_.send(data))

  def internalConnect(config: Config): Unit = {
    logger.info(s" connecting to: ${config.url}")

    socket = Some(new WebSocket(config.url))

    socket.foreach { s =>
      s.onopen = (event: Event) => internalOnConnect(event)
      s.onclose = (event: Event) => internalOnDisconnect(event)
      s.onerror = (errorEvent: ErrorEvent) => internalOnError(errorEvent)
      s.onmessage = (messageEvent: MessageEvent) => internalOnMessage(messageEvent)
    }

  }

  def encode(value: String): TransportSendMessage = value
  def decode(message: TransportReceiveMessage): String = message.data.toString

  onConnect(e => info(s"connection was successful e: ${JSON.stringify(e)}"))
  onDisconnect(e => info(s"disconnected e: ${JSON.stringify(e)}"))
  onError(e => warn(s"transport error e: ${JSON.stringify(e)}"))
  onRawMessage(m => info(s"message received m: ${JSON.stringify(m)}"))
  onMessage(m => info(s"message received m: '$m'"))
  onSendMessage(m => info(s"send message m: '$m'"))
}

object WebSocketTransport {
  def apply(config: WebSocketConfig): Transport = {
    val t = new WebSocketTransport(){}
    t.setConfig(config)
    t
  }

  implicit object eventEncoder extends TransportEncoder[Event, String] {
    override def transform(value: Event): String = JSON.stringify(value)
  }
}