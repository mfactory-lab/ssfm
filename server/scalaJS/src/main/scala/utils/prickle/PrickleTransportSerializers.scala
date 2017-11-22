/**
  * Copyright 2017, Alexander Ray (dev@alexray.me)
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  **/

package utils.prickle

import prickle.{Pickle, Pickler, Unpickle, Unpickler}
import shared.utils.{DecodeError, StringDecodeError, TransportDecoder, TransportEncoder}

import scala.collection.mutable
import scala.util.{Failure, Success, Try}

object PrickleTransportSerializers {

  implicit def prickleDecoder[T](implicit u: Unpickler[T]) = new TransportDecoder[T, String] {
    override def transform(value: String): Either[DecodeError, T] = {
      Unpickle[T].fromString(value, mutable.Map.empty) match {
        case Success(v) => Right(v)
        case Failure(e) => Left(StringDecodeError(e.getLocalizedMessage))
      }
    }
  }

  implicit def toPrickleEncoder[T](implicit p: Pickler[T]) =  new TransportEncoder[T, String] {
    override def transform(value: T): String = Pickle.intoString(value)
  }

}
