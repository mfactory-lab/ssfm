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

trait Listener[Event] {

  type EventListenerType = Event => Unit

  var listeners = List.empty[EventListenerType]

  def onEvent(f: EventListenerType): Unit = listeners :+= f
  def apply(f: EventListenerType): Unit = onEvent(f)

  def notify(event: Event): Unit = listeners.foreach(l => l(event))
}

object Listener {
  def apply[Event](): Listener[Event] = new Listener[Event] {}
}
