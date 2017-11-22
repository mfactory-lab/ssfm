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

//object test {
//
//
//  sealed trait Command
//  case class AddUser(userName: String) extends Command
//
//  sealed trait TransportMessage
//
//  case class PlainMessage(data: String) extends TransportMessage
//  case class Request(id: String, transportMessage: TransportMessage) extends TransportMessage
//
//
//
//  trait Encoder[S, D] {
//    def encode(value: S): D
//  }
//
//  implicit def commandEncoder[C <: Command]: Encoder[C, TransportMessage] =
//    new Encoder[C, TransportMessage]
//    {
//    override def encode(value: C): TransportMessage = {
//      PlainMessage(value.toString)
//    }
//  }
//
//  implicit object tmEncoder extends Encoder[TransportMessage, String] {
//    override def encode(value: TransportMessage): String = value.toString
//  }
//
//
//  def compositeEncoder[S, M, D]
//    (implicit e1: Encoder[S, M], e2: Encoder[M, D]): Encoder[S, D] = new Encoder[S, D]
//  {
//    override def encode(value: S): D = e2.encode(e1.encode(value))
//  }
//
//  implicit def ce[C <: Command]: Encoder[C, String] = compositeEncoder(commandEncoder, tmEncoder)
//
//  def send[S <: Command](value: S)(implicit encoder: Encoder[S, String]): String = ???
//
//  def sendRequest[S <: Command](value: S)
//                               (implicit e1: Encoder[S, TransportMessage],
//                                e2: Encoder[TransportMessage, String]): String =
//  {
//    val r: TransportMessage = Request("1", e1.encode(value))
//    e2.encode(r)
//  }
//
//  val s1: String = send(AddUser("u1"))
//  val s2: String = sendRequest(AddUser("u1"))
//
////  val s1: String = transform(IntHolder(10)) // don't work
////  val s2: String = transform(IntHolder(10))(compositeEncoder[IntHolder, StringHolder, String])
//
////  transform(10)(compositeEncoder[Int, IntHolder, String]()(implicitly, implicitly)) // don't work
////  transform(10)(compositeEncoder[Int, IntHolder, String](implicitly, compositeEncoder[IntHolder, StringHolder, String]))
//
//}


trait TransportDecorator[S, IN, OUT, T] {
  def decorate(message: S)(implicit serializer: TransportSerializer[S, S, T]): OUT
  def unDecorate(message: IN)(implicit serializer: TransportSerializer[S, S, T]): Either[DecodeError, S]
}

trait StringTransportDecorator[S, IN, OUT] extends TransportDecorator[S, IN, OUT, String]

trait SimpleStringTransportDecorator[S, D] extends StringTransportDecorator[S, D, D]

trait IdentityStringTransportDecorator[S] extends SimpleStringTransportDecorator[S, S]

object TransportDecorator {

//  def identityDecorator[S]() = new IdentityStringTransportDecorator[S] {
//    override def decorate(message: S)
//                         (implicit serializer: TransportSerializer[S, S, String]): S = message
//
//    override def unDecorate(message: S)
//                           (implicit serializer: TransportSerializer[S, S, String]): Either[DecodeError, S] = Right(message)
//  }
//
//  def plainMessageDecorator[S]() = new SimpleStringTransportDecorator[S, PlainMessage] {
//    override def decorate(message: S)
//                         (implicit serializer: TransportSerializer[S, S, String]): PlainMessage = {
//      PlainMessage(serializer.encode(message))
//    }
//
//    override def unDecorate(message: PlainMessage)
//                           (implicit serializer: TransportSerializer[S, S, String]): Either[DecodeError, S] =
//    {
//      serializer.decode(message.data)
//    }
//  }
//
//  def requestResponseDecorator[S]() = new StringTransportDecorator[S, ResponseMessage, RequestMessage] {
//    override def decorate(message: S)
//                         (implicit serializer: TransportSerializer[S, S, String]): RequestMessage =
//    {
//      RequestMessage(serializer.encode(message))
//    }
//
//    override def unDecorate(message: ResponseMessage)
//                           (implicit serializer: TransportSerializer[S, S, String]): Either[DecodeError, S] =
//    {
//      serializer.decode(message.responseData)
//    }
//  }

}