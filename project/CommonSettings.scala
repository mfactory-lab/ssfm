/**
  * Copyright 2016, Alexander Ray (dev@alexray.me)
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  **/

import Dependencies.{resolvers => _, _}
import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.AutoImport._
import org.scalajs.sbtplugin.cross.CrossProject
import play.sbt.PlayScala
import sbt.Keys._
import sbt._
import sbtbuildinfo.BuildInfoPlugin
import webscalajs.ScalaJSWeb

object CommonSettings {

  val javacOptionsS = Seq(
    "-source", "1.8",
    "-target", "1.8"
  )

  // Scala Compiler Options
  val scalacOptionsS = Seq(
    "-target:jvm-1.8",
    "-encoding", "UTF-8",
    "-deprecation", // warning and location for usages of deprecated APIs
    "-feature", // warning and location for usages of features that should be imported explicitly
    "-unchecked", // additional warnings where generated code depends on assumptions
    "-Xlint", // recommended additional warnings
    "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver
    "-Ywarn-value-discard", // Warn when non-Unit expression results are unused
    "-Ywarn-inaccessible",
    "-Ywarn-dead-code",
    "-Xlog-implicits"
  )

  val projectSettings = Seq(
    organization := "me.alexray",
    version := "0.0.2.SNAPSHOT",
    scalaVersion := Dependencies.scalaV,
    resolvers ++= Dependencies.resolvers,
    parallelExecution in Test := true,
    crossPaths := false
  )

  def projectName(moduleName: String): String = moduleName

  def PlayProject(moduleName: String, fileName: String): Project = (
    BaseProject(moduleName, fileName)
      enablePlugins(PlayScala, BuildInfoPlugin)
    )

  def BaseProject(moduleName: String, fileName: String): Project = (
    Project(projectName(moduleName), file(fileName))
      settings (projectSettings: _*)
    )

  def ServerScalaJsProject(moduleName: String, fileName: String): Project = (
    BaseProject(moduleName, fileName)
      enablePlugins(ScalaJSPlugin, ScalaJSWeb)
    )
    .settings(
      scalaJSStage in Global := FastOptStage
    )
    .settings(
      libraryDependencies ++=
        scalaJs.crossDependencies.value ++
          scalaJsDependencies
    )

  def ClientScalaJSProject(moduleName: String, fileName: String): Project = (
    BaseProject(moduleName, fileName)
      enablePlugins ScalaJSPlugin
      settings (
        name := "Scala.js"
      )
      settings (
        scalaJSStage in Global := FastOptStage,
        skip in packageJSDependencies := true,
        scalaJSModuleKind := ModuleKind.CommonJSModule
      )
      settings(
        libraryDependencies ++= logging.wvlet.value ++ circe.circeCrossDependencies.value
      )
    )

  def SharedJsProject(moduleName: String, fileName: String): CrossProject = (
    CrossProject(projectName(moduleName), file(fileName), org.scalajs.sbtplugin.cross.CrossType.Pure)
      settings (projectSettings: _*)
      settings(
      libraryDependencies ++=
        sharedJs.sharedDependencies.value ++
          sharedJsDependencies ++
          circe.circeCrossDependencies.value
      )
      jsConfigure (_ enablePlugins ScalaJSWeb)
    )
}
