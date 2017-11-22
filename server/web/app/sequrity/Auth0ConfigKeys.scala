/**
  * Copyright 2017, Alexander Ray (dev@alexray.me)
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  */

package sequrity

import javax.inject.Inject

import play.api.Environment
import play.api.Configuration

class Auth0ConfigKeys @Inject()(config: Configuration, environment: Environment) {
  val clientId: String = config.underlying.getString("authentication.auth0.clientId")
  val clientSecret: String = config.underlying.getString("authentication.auth0.clientSecret")
  val domain: String = config.underlying.getString("authentication.auth0.domain")

  private val prodPrefix: String = if (environment.mode == play.api.Mode.Prod) "prod." else ""
  val redirectURI: String = config.underlying.getString(prodPrefix + "authentication.auth0.redirectURI")
  val logoutCallback: String = config.underlying.getString(prodPrefix + "authentication.auth0.logoutCallback")
}