/**
  * Copyright 2016, Alexander Ray (dev@alexray.me)
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  **/

package utils

import com.greencatsoft.angularjs.core.Scope
import org.scalajs.dom._

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{Future, Promise}
import scala.reflect.ClassTag
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js.annotation.JSExportAll
import scala.util.{Failure, Success, Try}

object Utils {
  def flatten[T](future: Future[Try[T]]): Future[T] = future flatMap handleTry

  def handleTry[T](t: Try[T]): Future[T] = t match {
    case Success(s) => Future.successful(s)
    case Failure(f) => Future.failed(f)
  }

  def handleError(t: Throwable) {
    console.error(s"An error has occured: $t")
  }

  implicit class FutureCompanionObjectExtension (val f: Future.type ) extends AnyVal {

    def delayedFuture[T](millis: FiniteDuration)(block: => T): Future[T] = {
      val p = Promise[T]()

      scala.scalajs.js.timers.setTimeout(millis) {
        p.complete(Try(block))
      }

      p.future
    }
  }

  implicit class FutureObjectExtension[T: ClassTag](f: Future[T]) {

    def applyToScope(action: T => Unit)(implicit scope: Scope): Unit =
      f.foreach { value =>
        scope.$apply {
          action(value)
        }
      }

  }


}
