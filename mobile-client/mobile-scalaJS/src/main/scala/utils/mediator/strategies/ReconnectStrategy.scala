/**
  * Copyright 2016, Alexander Ray (dev@alexray.me)
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  **/

package utils.mediator.strategies

import utils.mediator.strategies.ReconnectStrategy.Transports
import utils.mediator.transport.TransportFactory

import scala.collection.immutable.Queue
import scala.language.higherKinds

trait ReconnectStrategy {

  def process(transports: Transports): (Option[TransportFactory], Transports)

  def apply(transports: Transports)
             (block: TransportFactory => Unit): Transports = {
    val (f, ts) = process(transports)
    f.foreach(block)
    ts
  }
}

object ReconnectStrategy {
  type Transports = Queue[TransportFactory]

  implicit val defaultStrategy: ReconnectStrategy = roundRobinStrategy
}

object roundRobinStrategy extends ReconnectStrategy {
  override def process(transports: Transports): (Option[TransportFactory], Transports) = {
    if (transports.isEmpty) {
      (None, transports)
    } else {
      val (tf, ts) = transports.dequeue
      (Some(tf), ts.enqueue(tf))
    }
  }
}


