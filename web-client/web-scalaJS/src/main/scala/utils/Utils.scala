/**
  * Copyright 2017, Alexander Ray (dev@alexray.me)
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  */

package utils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag
import scala.scalajs.js
import scala.scalajs.js.{Promise, Thenable, |}

object Utils {

  implicit class FutureObjectExtension[T: ClassTag](f: Future[T]) {

    def toDefaultPromise: Promise[T] = toPromise({
      _.getMessage
    })

    def toPromise(recovery: Throwable => js.Any)
                 (implicit ec: ExecutionContext): scala.scalajs.js.Promise[T] = {
      new scala.scalajs.js.Promise[T](
        (resolve: js.Function1[T | Thenable[T], _], reject: js.Function1[scala.Any, _]) => {
          f.onSuccess({
            case x => resolve(x)
          })
          f.onFailure({
            case e => reject(recovery(e))
          })
        }
      )
    }

  }

}