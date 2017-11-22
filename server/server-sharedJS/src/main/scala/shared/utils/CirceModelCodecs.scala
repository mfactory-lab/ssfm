/**
  * Copyright 2017, Alexander Ray (dev@alexray.me)
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  */

package shared.utils

import io.circe._
import io.circe.parser._
import shared.entities.ChatEntities.CirceSerializable

object CirceModelCodecs {

//  private def _toWebSocketEncoder[T]()(implicit encoder: Encoder[T]): WebSocketEncoder[T] = new WebSocketEncoder[T] {
//    override def encode(value: T): String = {
//      encoder(value).noSpaces
//    }
//  }
//
//  private def _toWebSocketDecoder[T]()(implicit decoder: Decoder[T]): WebSocketDecoder[T] = new WebSocketDecoder[T] {
//    override def decode(value: String): Either[String, T] = {
//      val x: Either[Error, T] = parse(value).right.flatMap { json =>
//        decoder.decodeJson(json)
//      }
//      val y: Either[String, T] = x.left.map{ e => e.getLocalizedMessage}
//      y
//    }
//  }
//
//  implicit def toWebSocketEntityEncoder[T <: CirceSerializable](implicit encoder: Encoder[T]): WebSocketEncoder[T] = _toWebSocketEncoder()
//  implicit def toWebSocketEntityDecoder[T <: CirceSerializable](implicit decoder: Decoder[T]): WebSocketDecoder[T] = _toWebSocketDecoder()

}
