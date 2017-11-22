/**
  * Copyright 2016, Alexander Ray (dev@alexray.me)
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  **/

import chat.controllers._
import chat.directives._
import chat.services.{CommunicationServiceFactory, ModelServiceFactory, TransportServiceFactory, UtilsServiceFactory}
import com.greencatsoft.angularjs.Angular
import utils.mediator.Communicator
import utils.mediator.transport._
import visualize.controllers.MainController
import wvlet.log._

import scala.scalajs.js.JSApp

//object Test {
//
//  class ActorWithStash extends Actor with Stash with ActorLogging {
//    override def receive: Receive = {
//      case msg =>
//        log.info(s"msg: $msg")
//    }
//  }
//
//  lazy val _config: Config =
//    ConfigFactory
//      .parseString("""
//        stash-custom-mailbox {
//          mailbox-type = "akka.dispatch.UnboundedDequeBasedMailbox"
//        }
//        """
//      ).withFallback(akkajs.Config.default)
//
//  val system: ActorSystem = ActorSystem("wsSystem", _config)
//
//  def testStashActor() = {
//    val actorWithStash =
//      system.actorOf(Props(new ActorWithStash()).withMailbox("stash-custom-mailbox"), "ActorWithStash")
//    actorWithStash ! "TestMessage"
//  }
//
//
//}

object App extends JSApp with LogSupport {

  Logger.setDefaultHandler(JSConsoleLogHandler())

  def bootStrapAngular(): Unit = {
    val module = Angular.module("ssfm-module.scalajs-app",
      Seq(
        "ssfm-module.common.services.playRoutesService"
      )
    )

    module
      .controller[MainController]
      .controller[ChatController]
      .controller[MessengerController]
      .controller[DialogsController]
      .controller[LoginController]
      .controller[SettingsController]
      .controller[UsersController]
      .factory[ModelServiceFactory]
      .factory[CommunicationServiceFactory]
      .factory[UtilsServiceFactory]
      .factory[TransportServiceFactory]
      .directive[UsersListDirective]
      .directive[DialogsListDirective]
      .directive[SettingsListDirective]
      .directive[TabFocusDirective]
      .directive[ScrollBottomDirective]
  }

  override def main(): Unit = {

//    Test.testStashActor()

    bootStrapAngular()

    info("Scala-App-Module - initialized")
  }

}
