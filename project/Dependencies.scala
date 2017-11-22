/**
  * Copyright 2017, Alexander Ray (dev@alexray.me)
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  **/

import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt._

object Dependencies {

  val scalaV = "2.11.8"

  val resolvers = DefaultOptions.resolvers(snapshot = true) ++ Seq(
    "Restlet Repository" at "http://maven.restlet.org"
  )

  object scalaModules {
    val dependencies = Seq(
    )
  }

  object testing {
    val dependencies: Seq[ModuleID] = Seq(
    )
  }

  object logging {
    val slf4j = "com.typesafe.akka" %% "akka-slf4j" % "2.4.17"
    val logback = "ch.qos.logback" % "logback-classic" % "1.2.2"
    val wvlet = Def.setting(Seq("org.wvlet" %%% "wvlet-log" % "1.2.2"))

    val dependencies: Seq[ModuleID] = Seq(slf4j, logback)
  }

  object akka {

    val version = "2.4.17"

    val dependencies = Def.setting(
      Seq(
        "com.typesafe.akka" %% "akka-actor" % version exclude("com.typesafe", "config")
      )
    )
  }

  object webjars {
    val webjarsPlay = "org.webjars" %% "webjars-play" % "2.5.0"
    val requireJs = "org.webjars" % "requirejs" % "2.3.3"
    val bootstrap = "org.webjars" % "bootstrap" % "3.3.7"
    val jquery = "org.webjars" % "jquery" % "3.2.0"
    val adminLTE = "org.webjars.bower" % "adminlte" % "2.3.11"
    val fontAwesome = "org.webjars.bower" % "font-awesome" % "4.7.0"

    val angularJsVersion = "1.5.9"

    val angular = "org.webjars.bower" % "angular" % angularJsVersion exclude("org.webjars", "jquery")

    val dependencies: Seq[ModuleID] = Seq(
      webjarsPlay,
      requireJs,
      angular,
      bootstrap,
      jquery,
      adminLTE,
      fontAwesome
    )
  }


  object circe {
    val circeVersion = "0.7.0"

    val dependencies = Seq(
      "io.circe" %% "circe-core",
      "io.circe" %% "circe-generic",
      "io.circe" %% "circe-parser"
    ).map(_ % circeVersion)

    val circeCrossDependencies = Def.setting(
      Seq(
        "io.circe" %%% "circe-core",
        "io.circe" %%% "circe-generic",
        "io.circe" %%% "circe-parser"
      ).map(_ % circe.circeVersion)
    )

  }

  object scalaJs {

    val angularVersion = "0.7"
    val scalaJSVersion = "0.6.15"
    val akkaJSVersion = "0.2.4.17"
    val scalajsDomVersion = "0.9.1"
    val scalaTagsVersion = "0.6.3"

    val stubs = "org.scala-js" %% "scalajs-stubs" % scalaJSVersion % "provided"

    val crossDependencies = Def.setting(
      Seq(
        "io.github.cquiroz" %%% "scala-java-time" % "2.0.0-M9",
        "com.greencatsoft" %%% "scalajs-angular" % angularVersion,
        "org.scala-js" %%% "scalajs-dom" % scalajsDomVersion,
        "com.lihaoyi" %%% "scalatags" % scalaTagsVersion,
        "org.wvlet" %%% "wvlet-log" % "1.2.2",
        "com.github.benhutchison" %%% "prickle" % "1.1.14"
        )
        ++
      Seq(
        "io.circe" %%% "circe-core",
        "io.circe" %%% "circe-generic",
        "io.circe" %%% "circe-parser"
      ).map(_ % circe.circeVersion)

    )

    val dependencies = Seq (
      stubs
    )

  }

  object sharedJs {

    val dependencies = Seq(
      scalaJs.stubs
    )

    val sharedDependencies = Def.setting(
      Seq(
      )
    )

  }

  object playFramework {
    val version      = play.core.PlayVersion.current

    val cache        = "com.typesafe.play"   %% "play-cache"               % version
    val ws           = "com.typesafe.play"   %% "play-ws"                  % version

    val dependenciesOnScalaJs: Seq[ModuleID] = Seq(
      scalaJs.stubs
    )

    val dependencies: Seq[ModuleID] = Seq(
      cache,
      ws
    ) ++ circe.dependencies
  }

  object gitDependencies {
  }

  val webDependencies: Seq[ModuleID] =
    logging.dependencies ++
    playFramework.dependencies ++
    playFramework.dependenciesOnScalaJs ++
    webjars.dependencies

  val commonDependencies: Seq[ModuleID] =
    scalaModules.dependencies ++
    testing.dependencies

  val scalaJsDependencies: Seq[ModuleID] =
    scalaJs.dependencies

  val sharedJsDependencies: Seq[ModuleID] =
    sharedJs.dependencies
}
