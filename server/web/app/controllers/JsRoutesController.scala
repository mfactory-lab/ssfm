package controllers

import javax.inject.Inject

import play.api.cache.Cached
import play.api.mvc.{Action, Controller}
import play.api.routing.JavaScriptReverseRoute

/**
  * Copyright 2016, Alexander Ray
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  **/

class JsRoutesController @Inject() (cached: Cached) extends Controller {

  def listRoutes[T](c: Class[T]) = {
    val jsRoutesClass = c
    val controllers = jsRoutesClass.getFields.map(_.get(null))

    controllers.flatMap { controller =>
      controller.getClass.getDeclaredMethods.
        filter(_.invoke(controller).isInstanceOf[play.api.routing.JavaScriptReverseRoute] ).
        map(_.invoke(controller).asInstanceOf[play.api.routing.JavaScriptReverseRoute])
    }
  }

  lazy val routeCache: Array[JavaScriptReverseRoute] = {

    Array()
//    listRoutes(classOf[controllers.api.routes.javascript])

  }

  def jsRoutes(varName: String = "jsRoutes") = cached(_ => "jsRoutes", duration = 86400) {

    Action { implicit request =>
      Ok(play.api.routing.JavaScriptReverseRouter(varName)(routeCache: _*)).as(JAVASCRIPT)
    }
  }


}
