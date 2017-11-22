/**
  * Copyright 2016, Alexander Ray (dev@alexray.me)
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  **/

package shared

import java.util.UUID

object Common {

  sealed trait TransportMessage extends Serializable

  case class UID(value: String) extends TransportMessage

  object UID {
    def apply(): UID = UID(generateUid())
  }

  def generateUid(): String = UUID.randomUUID().toString.takeRight(12)
  def randomizeName(name: String): String = name + '_' + UUID.randomUUID().toString.takeRight(12)


  case class RequestMessage(uid: UID, transportMessage: TransportMessage) extends TransportMessage

  object RequestMessage {
    def apply(transportMessage: TransportMessage): RequestMessage = RequestMessage(UID(), transportMessage)
  }

  case class ResponseMessage(uid: UID, transportMessage: TransportMessage) extends TransportMessage
  case class PlainMessage(data: String) extends TransportMessage

}