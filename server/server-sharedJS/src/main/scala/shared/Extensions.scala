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

import scala.concurrent.Future
import scala.language.implicitConversions
import scala.util.{Failure, Success, Try}

object Extensions {

  implicit class TryObjectExtensions[T](value: Try[T]) {
    def toFuture: Future[T] =
      value match{
        case Success(s) => Future.successful(s)
        case Failure(ex) => Future.failed(ex)
      }
  }


}
