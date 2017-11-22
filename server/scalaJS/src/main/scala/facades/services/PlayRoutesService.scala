/**
  * Copyright 2016, Alexander Ray (dev@alexray.me)
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  **/

package facades.services

import com.greencatsoft.angularjs.injectable
import scala.scalajs.js

@js.native
@injectable("playRoutesService")
trait PlayRoutesService extends js.Object {

  val controllers: js.Dynamic = js.native

}
