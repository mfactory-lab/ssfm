/**
  * Copyright 2017, Alexander Ray (dev@alexray.me)
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  **/


package shared.utils

import io.circe.parser.parse
import io.circe.{Decoder, Encoder, Error, Json}
import io.circe.syntax._
import shared.Common.{PlainMessage, TransportMessage, UID}
import shared.entities.ChatEntities.CirceSerializable
import io.circe.generic.auto._
import io.circe._
import io.circe.generic.semiauto._

object CirceTransportSerializers  {

  type TM = TransportMessage
  type CS = CirceSerializable

  implicit val uidDecoder: Decoder[UID] = deriveDecoder[UID]
  implicit val uidEncoder: Encoder[UID] = deriveEncoder[UID]

  implicit val tmDecoder: Decoder[TransportMessage] = deriveDecoder[TransportMessage]
  implicit val tmEncoder: Encoder[TransportMessage] = deriveEncoder[TransportMessage]

  implicit val csDecoder: Decoder[CirceSerializable] = deriveDecoder[CirceSerializable]
  implicit val csEncoder: Encoder[CirceSerializable] = deriveEncoder[CirceSerializable]

  def withCirceDecoder[T](implicit decoder: Decoder[T]) = new TransportDecoder[T, PlainMessage] {
    override def transform(value: PlainMessage): Either[StringDecodeError, T] = {
      val x: Either[Error, T] = parse(value.data).right.flatMap { json =>
        decoder.decodeJson(json)
      }
      val y: Either[StringDecodeError, T] = x.left.map{ e => StringDecodeError(s"${e.getLocalizedMessage} - '$value'")}
      y
    }
  }

  implicit def withCirceDecoderCS[T <: CS](implicit decoder: Decoder[T]): TransportDecoder[T, PlainMessage] =
    new TransportDecoder[T, PlainMessage]
  {
    override def transform(value: PlainMessage): Either[StringDecodeError, T] = {
      val x: Either[Error, T] = parse(value.data).right.flatMap { json =>
        decoder.decodeJson(json)
      }
      val y: Either[StringDecodeError, T] = x.left.map{ e => StringDecodeError(s"${e.getLocalizedMessage} - '$value'")}
      y
    }
  }

  implicit def withCirceDecoderTM[T <: TM](implicit decoder: Decoder[T]): TransportDecoder[T, String] =
    new TransportDecoder[T, String]
  {
    override def transform(value: String): Either[StringDecodeError, T] = {
      val x: Either[Error, T] = parse(value).right.flatMap { json =>
        decoder.decodeJson(json)
      }
      val y: Either[StringDecodeError, T] = x.left.map{ e => StringDecodeError(s"${e.getLocalizedMessage} - '$value'")}
      y
    }
  }

  implicit def composedDecoder[T1 <: CS]
    (implicit d1: Decoder[T1], d2: Decoder[TransportMessage]): TransportDecoder[T1, String] =
  new TransportDecoder[T1, String] {
    override def transform(value: String): Either[DecodeError, T1] = {
      withCirceDecoderTM[TransportMessage](d2).decode(value).right.flatMap {
        case pm: PlainMessage => withCirceDecoderCS[T1].decode(pm)
        case m => Left(StringDecodeError(s"required PlainMessage received: '$m'"))
      }
    }
  }

  implicit def withTMCirceEncoder[T <: TM](implicit encoder: Encoder[TM]): TransportEncoder[T, String] =
    new TransportEncoder[T, String]
  {
    override def transform(value: T): String = encoder(value).noSpaces
  }

  implicit def withCSCirceEncoder[T <: CS](implicit encoder: Encoder[CS]): TransportEncoder[T, PlainMessage] =
    new TransportEncoder[T, PlainMessage]
  {
    override def transform(value: T): PlainMessage = {
      PlainMessage(encoder(value).noSpaces)
    }
  }

  implicit def composedEncoder[T <: CS]: TransportEncoder[T, String] =
    TransportEncoder.compose[T, PlainMessage, String](withCSCirceEncoder, withTMCirceEncoder)

  def jsonWrapClass[T](t: T)(implicit encoder: Encoder[T]): Json =
    Map(t.getClass.getSimpleName -> t).asJson


}
