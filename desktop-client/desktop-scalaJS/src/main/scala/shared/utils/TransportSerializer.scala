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

import scala.language.higherKinds

sealed trait DecodeError

case class StringDecodeError(value: String) extends DecodeError {
  override def toString: String = value
}

trait MessageTransformer[S, D] {
  def transform(value: S): D
}

object MessageTransformer {
  def compose[S, M, D](implicit e1: MessageTransformer[S, M], e2: MessageTransformer[M, D]): MessageTransformer[S, D] =
    new MessageTransformer[S, D]
    {
      override def transform(value: S): D = e2.transform(e1.transform(value))
    }
}

trait TransportEncoder[S, T] extends MessageTransformer[S, T] {
  def encode(value: S): T = transform(value)
}

object TransportEncoder {
  def compose[S, M, D](implicit e1: TransportEncoder[S, M], e2: TransportEncoder[M, D]): TransportEncoder[S, D] =
    new TransportEncoder[S, D]
    {
      override def transform(value: S): D = e2.transform(e1.transform(value))
    }

//  def prepend[S, M, D](e1: TransportEncoder[S, M])(e2: TransportEncoder[M, D]): TransportEncoder[S, D]
}

trait TransportDecoder[D, T] extends MessageTransformer[T, Either[DecodeError, D]]{
  def decode(value: T): Either[DecodeError, D] = transform(value)
}

object TransportDecoder {

  def compose[D, M, S](implicit e1: TransportDecoder[M, S], e2: TransportDecoder[D, M]): TransportDecoder[D, S] =
    new TransportDecoder[D, S]
    {
      override def transform(value: S): Either[DecodeError, D] =
        e1.transform(value).right.flatMap(e2.transform)
    }

}

trait TransportSerializer[S, D, T] {
  def encoder: TransportEncoder[S, T]
  def decoder: TransportDecoder[D, T]

  def encode(value: S): T
  def decode(value: T): Either[DecodeError, D]
}

object TransportSerializer {

  implicit def ts[S, D, T](implicit te: TransportEncoder[S, T], td: TransportDecoder[D, T]) = new TransportSerializer[S, D, T] {
    override def encode(value: S): T = encoder.encode(value)
    override def decode(value: T): Either[DecodeError, D] = decoder.decode(value)
    override def encoder: TransportEncoder[S, T] = te
    override def decoder: TransportDecoder[D, T] = td
  }

}

trait LowPriorityTransportSerializers {
  implicit object stringEncoder extends TransportEncoder[String, String] {
    override def transform(value: String): String = value
  }
  implicit object stringDecoder extends TransportDecoder[String, String] {
    override def transform(value: String): Either[DecodeError, String] = Right(value)
  }
}