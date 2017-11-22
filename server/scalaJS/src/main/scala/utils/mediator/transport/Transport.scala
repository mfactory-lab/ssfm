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

import utils.mediator.Listener
import wvlet.log.LogSupport

import scala.scalajs.js.timers._

trait TransportFactory {

  def createTransport(): Transport
}

trait Transport extends LogSupport {

  type Config
  type TransportEvent
  type TransportError
  type TransportReceiveMessage
  type TransportSendMessage

  private val connectEventListeners = Listener[TransportEvent]()
  private val disconnectEventListeners = Listener[TransportEvent]()
  private val errorEventListeners = Listener[TransportError]()
  private val receiveMessageEventListeners = Listener[TransportReceiveMessage]()
  private val sendRawMessageListeners = Listener[TransportSendMessage]()
  private val sendStringMessageListeners = Listener[String]()

  type StringMessageEventListener = String => Unit

  def timeout: Int
  def tryReconnect: Boolean

  private var internalConnected = false

  private var currentConfig: Option[Config] = None

  def connected: Boolean = internalConnected

  def internalOnConnect(event: TransportEvent): Unit = {
    internalConnected = true
    connectEventListeners.notify(event)
  }

  def internalOnDisconnect(event: TransportEvent): Unit = {
    internalConnected = false
    disconnectEventListeners.notify(event)
    if (tryReconnect) reconnect()
  }

  def internalOnError(error: TransportError): Unit = errorEventListeners.notify(error)
  def internalOnMessage(message: TransportReceiveMessage): Unit = receiveMessageEventListeners.notify(message)

  def internalConnect(config: Config): Unit
  def internalSend(data: TransportSendMessage): Unit

  def send(data: String): Unit = {
    val encoded = encode(data)
    sendRawMessageListeners.notify(encoded)
    internalSend(encoded)
  }

  def setConfig(config: Config): Unit = {
    currentConfig = Some(config)
  }

  def reconnect(): Unit = {
    logger.info(s"will try to reconnect in $timeout ms")

    setTimeout(timeout) { currentConfig.foreach(internalConnect) }
  }

  def connect(): Unit = {
    logger.info(s" connecting to: $currentConfig")
    currentConfig.foreach(internalConnect)
  }

  def onConnect(f: connectEventListeners.EventListenerType): Unit = connectEventListeners(f)
  def onDisconnect(f: disconnectEventListeners.EventListenerType): Unit = disconnectEventListeners(f)
  def onError(f: errorEventListeners.EventListenerType): Unit = errorEventListeners(f)
  def onRawMessage(f: receiveMessageEventListeners.EventListenerType): Unit = {
    info(s"added onRawMessage: ${receiveMessageEventListeners.listeners.size}")
    receiveMessageEventListeners(f)
  }
  def onMessage(f: sendStringMessageListeners.EventListenerType): Unit = sendStringMessageListeners(f)
  def onSendMessage(f: sendRawMessageListeners.EventListenerType): Unit = sendRawMessageListeners(f)

  onRawMessage(x => sendStringMessageListeners.notify(decode(x)))

  def encode(value: String): TransportSendMessage
  def decode(message: TransportReceiveMessage): String

}
