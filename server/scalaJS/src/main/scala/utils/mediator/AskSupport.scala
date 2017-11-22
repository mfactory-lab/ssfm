/**
  * Copyright 2017, Alexander Ray (dev@alexray.me)
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  */

package utils.mediator

import shared.Common._
import shared.utils.{DecodeError, TransportDecoder, TransportEncoder, TransportSerializer}
import wvlet.log.LogSupport

import scala.concurrent.{Future, Promise}

//trait AskSupport extends LogSupport { c: Communicator =>
//
//  private val ts = c.ts
//
//  type ResponseProcessor = ResponseMessage => Unit
//
//  private var requests = Map.empty[UID, ResponseProcessor]
//
//  c.onDecodedMessage[TransportMessage]{
//    case m: ResponseMessage =>
//      requests.get(m.uid).foreach { f => f(m) }
//    case _ =>
//  }(ts.)
//
//
////  def ?[Request, Response](q: Request)
////                          (implicit askSerializer: TransportSerializer[Request, Response, String]): Future[Response] =
////    explicitAsk(q)(askSerializer)
//
//  def explicitAsk[Request, Response](q: Request)
//                            (implicit askSerializer: TransportSerializer[Request, Response, TransportMessage],
//                             ts: TransportSerializer[TransportMessage, TransportMessage, String]):
//  Future[Response] =
//  {
//    info(s"def ask[Request, Response](q: Request) - '$q'")
//    val uid = UID()
//    val request: TransportMessage  = RequestMessage(uid, askSerializer.encode(q))
//
//    val p = Promise[Response]()
//
//    val f: ResponseProcessor = (response: ResponseMessage) => {
//      info(s"ResponseProcessor - $response")
//      info(s"decoder - $askSerializer")
//      askSerializer.decode(response.transportMessage) match {
//        case Left(e: DecodeError) =>
//          error(s"decoding failed: $e")
//          p.failure(new Throwable(e.toString))
//        case Right(m) =>
//          info(s"decoded: $m")
//          p.success(m)
//      }
//    }
//
//    requests += uid -> f
//
//    val x = ts.encode(request)
//    info(s"request $x")
//
//    c.send(request)(ts.encoder)
//
//    p.future
//  }
//
//
//}




