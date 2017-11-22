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

import scala.concurrent.{ExecutionContext, Future}

object Common {

  implicit class FutureCompanionObjectExtension(val f: Future.type ) extends AnyVal {

    def serializeFutures[A, B](l: Iterable[A])(fn: A => Future[B])
                              (implicit ec: ExecutionContext): Future[List[B]] =
      l.foldLeft(Future(List.empty[B])) {
        (previousFuture, next) =>
          for {
            previousResults <- previousFuture
            next <- fn(next)
          } yield {
            println(s"next - $next")
            previousResults :+ next
          }
      }
  }

}
