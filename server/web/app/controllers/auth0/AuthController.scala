/**
  * Copyright 2017, Alexander Ray (dev@alexray.me)
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  */

package controllers.auth0

import javax.inject.Inject

import controllers.chat.ChatController
import play.api.Configuration
import play.api.cache.CacheApi
import play.api.http.{HeaderNames, MimeTypes}
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.WSClient
import play.api.mvc._
import sequrity.Auth0ConfigKeys

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuthController @Inject()(authConfig: Auth0ConfigKeys,
                               cache: CacheApi,
                               config: Configuration,
                               wsClient: WSClient,
                               chatController: ChatController) extends Controller {

  // callback route
  def callback(codeOpt: Option[String] = None): Action[AnyContent] = Action.async {
    codeOpt.map { code => {
      getToken(code).flatMap { case (idToken, accessToken) =>
        getUser(accessToken).map { user =>
          cache.set(idToken + "profile", user)
          chatController.setUserProfile(user)
          Redirect(controllers.routes.Application.messenger())
            .withSession(
              "idToken" -> idToken,
              "accessToken" -> accessToken
            )
        }

      }.recover {
        case ex: IllegalStateException => Unauthorized(ex.getMessage)
      }
    }
    }.getOrElse(Future.successful(BadRequest("No parameters supplied")))
  }

  def auth0logout = Action {
    Redirect(s"https://${authConfig.domain}/v2/logout?returnTo=${authConfig.logoutCallback}&client_id=${authConfig.clientId}")
  }

  def logoutCallback: Action[AnyContent] = Action { request =>
    chatController.onLogout()
    Redirect(controllers.routes.Application.messenger())
      .withSession {
        cache.remove(request.session.get("idToken") + "profile")
        new Session(request.session.data -- Seq("idToken", "accessToken"))
      }
  }

  def getToken(code: String): Future[(String, String)] = {
    val tokenResponse = wsClient.url(String.format("https://%s/oauth/token", authConfig.domain)).
      withHeaders(HeaderNames.ACCEPT -> MimeTypes.JSON).
      post(
        Json.obj(
          "client_id" -> authConfig.clientId,
          "client_secret" -> authConfig.clientSecret,
          "redirect_uri" -> authConfig.redirectURI,
          "code" -> code,
          "grant_type" -> "authorization_code"
        )
      )

    tokenResponse.flatMap { response =>
      (for {
        idToken <- (response.json \ "id_token").asOpt[String]
        accessToken <- (response.json \ "access_token").asOpt[String]
      } yield {
        Future.successful((idToken, accessToken))
      }).getOrElse(Future.failed[(String, String)](new IllegalStateException("Tokens not sent")))
    }

  }

  def getUser(accessToken: String): Future[JsValue] = {
    val userResponse = wsClient.url(String.format("https://%s/userinfo", authConfig.domain))
      .withQueryString("access_token" -> accessToken)
      .get()

    userResponse.flatMap(response => Future.successful(response.json))
  }
}