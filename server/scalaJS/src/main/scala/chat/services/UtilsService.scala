/**
  * Copyright 2017, Alexander Ray (dev@alexray.me)
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  */


package chat.services

import com.greencatsoft.angularjs.core.RootScope
import com.greencatsoft.angularjs.{Factory, Service, injectable}

import scala.scalajs.js

@injectable("utilsService")
class UtilsService(rootScope: RootScope) extends Service {

  def broadcastEvent(eventName: String, args: Any*): Unit = {
    val l: Seq[js.Any] = args.map(_.asInstanceOf[js.Any]).toList
    rootScope.$broadcast(eventName, l:_*)
  }

}

@injectable("utilsService")
class UtilsServiceFactory(rootScope: RootScope) extends Factory[UtilsService] {
  override def apply(): UtilsService = new UtilsService(rootScope)
}

