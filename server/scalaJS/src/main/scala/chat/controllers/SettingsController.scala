/**
  * Copyright 2017, Alexander Ray (dev@alexray.me)
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  */

package chat.controllers

import chat.services.{CommunicationService, SettingView}
import com.greencatsoft.angularjs.core.{Log, Scope}
import com.greencatsoft.angularjs.{AbstractController, injectable}

import scala.scalajs.js
import scala.scalajs.js.JSConverters._

object SettingsRefs {
  val one = "1"
  val two = "2"
  val three = "3"
  val logout = "4"
}

@js.native
trait SettingsScope extends Scope {
  var settings: js.Array[SettingView] = js.native
  var select: js.Function = js.native
}

@injectable("SettingsController")
class SettingsController(scope: SettingsScope,
                         log: Log,
                         modelService: CommunicationService)
  extends AbstractController[SettingsScope](scope) {

  scope.settings = List(
//    SettingView(SettingsRefs.one, "Setting 1", "Description 1"),
//    SettingView(SettingsRefs.two, "Setting 2", "Description 2"),
//    SettingView(SettingsRefs.three, "Setting 3", "Description 3"),
    SettingView(SettingsRefs.logout, "Logout", "Leave chat")
  ).toJSArray

  scope.select = (setting: SettingView) => {
    setting.ref match {
      case SettingsRefs.one =>
        log.info("Fired first action")
      case SettingsRefs.two =>
        log.info("Fired second action")
      case SettingsRefs.three =>
        log.info("Fired third action")
      case SettingsRefs.logout =>
        modelService.logout()
      case _ =>
        log.info("Unknown setting")
    }
  }

}