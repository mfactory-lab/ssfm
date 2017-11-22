/**
  * Copyright 2016, Alexander Ray (dev@alexray.me)
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  **/

package controllers

import javax.inject._

import akka.actor.{Actor, ActorLogging, ActorSystem, Props, Stash}
import com.typesafe.config
import com.typesafe.config.{Config, ConfigFactory}
import org.webjars.play.RequireJS
import play.api._
import play.api.cache.CacheApi
import play.api.libs.json.{JsString, JsValue, Json}
import play.api.mvc._
import sequrity.Auth0ConfigKeys


@Singleton
class Application @Inject()(cache: CacheApi,
                            webJarAssets: WebJarAssets,
                            requireJs: RequireJS,
                            environment: Environment,
                            configuration: Configuration,
                            auth0Config: Auth0ConfigKeys)
  extends Controller
{
  
  implicit val env = environment
  implicit val rjs = requireJs
  implicit val wjAssets = webJarAssets
  implicit val conf = configuration
  implicit val info = build.BuildInfo.toMap.map { case (k, v) => k -> v.toString }
  implicit val authConfig = auth0Config

  def login: Action[AnyContent] = Action {
    Ok(views.html.login())
  }

  def backdoor = Action {
    Ok(views.html.messenger(Json.obj("name" -> "backdoor_user")))
  }

  def messenger = AuthenticatedAction { request =>
    val idToken = request.session.get("idToken").get
    val profile = cache.get[JsValue](idToken + "profile").get
    Ok(views.html.messenger(profile))
  }

  def AuthenticatedAction(f: Request[AnyContent] => Result): Action[AnyContent] = {
    Action { request =>
      (request.session.get("idToken").flatMap { idToken =>
        cache.get[JsValue](idToken + "profile")
      } map { profile =>
        f(request)
      }).orElse {
        Some(Redirect(routes.Application.login()))
      }.get
    }
  }

}