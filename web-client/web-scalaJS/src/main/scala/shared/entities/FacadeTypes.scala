/**
  * Copyright 2017, Alexander Ray (dev@alexray.me)
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  */

package shared.entities

import scala.scalajs.js
import scala.scalajs.js.annotation.ScalaJSDefined

object FacadeTypes {

  @js.native
  trait SocketIO extends js.Object {
    def connect(): Unit = js.native

    def disconnect(): Unit = js.native

    def emit(event: String, value: js.Any): Unit = js.native

    def on(event: String, callback: js.Function): Unit = js.native
  }

  @js.native
  trait JsUserInfo extends js.Object {
    val name: String = js.native
    val avatarUrl: String = js.native
  }

  @ScalaJSDefined
  trait JsContactReference extends js.Object {
    val id: String
  }

  @ScalaJSDefined
  trait JsContactInfo extends js.Object {
    val contactReference: JsContactReference
    val name: String
    val avatar: String
  }

  @ScalaJSDefined
  trait JsDialogReference extends js.Object {
    val id: String
  }

  @ScalaJSDefined
  trait JsDialogInfo extends js.Object {
    val dialogReference: JsDialogReference
    val users: js.Array[JsContactInfo]
    val lastMessage: String
    val lastMessageDate: String
  }

  @ScalaJSDefined
  trait JsMessage extends js.Object {
    val dialogReference: JsDialogReference
    val contactInfo: JsContactInfo
    val value: String
    val date: String
  }

  @ScalaJSDefined
  trait JsSubject extends js.Object {
    def next(value: js.Object): js.Function
  }

  @ScalaJSDefined
  trait JsStatusChangedEvent extends js.Object {
    val contact: JsContactInfo
    val online: Boolean
  }
}
