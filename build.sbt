import CommonSettings._
import Dependencies._

import scala.language.postfixOps

scalaVersion in Scope.GlobalScope := scalaV

fork in run := true
publishMavenStyle := false
javaOptions in ThisBuild ++= Seq(
  "-Xms2g -Xmx2g -Xss2M -XX:MaxMetaspaceSize=1024M -XX:ReservedCodeCacheSize=1024m -XX:+UseCompressedOops"
)

val isWindows = System.getProperty("os.name").toLowerCase().contains("win")

def runScript(script: String)(implicit dir: File): Int = {
  if (isWindows) Process("cmd /c " + script, dir) else Process(script, dir)
} !

lazy val ssfm = RootProject(file("."))

// Server
lazy val web: Project = (
  PlayProject("web", "server/web")
    settings(
      libraryDependencies ++= webDependencies ++
        commonDependencies ++
        akka.dependencies.value
    )
    settings(
      publishMavenStyle := false,
      routesGenerator := InjectedRoutesGenerator,
      scalaJSProjects := jsProjects,
      pipelineStages in Assets := Seq(scalaJSPipeline),
      pipelineStages := Seq(digest, gzip),
      buildInfoPackage := "build",
      buildInfoOptions += BuildInfoOption.ToMap,
      buildInfoKeys ++= Seq (
        "projectName" -> "ssfm-server"
      ),
      herokuAppName in Compile := "ssfm-server",
      publishArtifact in (Compile, packageDoc) := false,
      publishArtifact in packageDoc := false,
      sources in (Compile, doc) := Seq.empty
    )
  ).
  dependsOn (server_sharedJS_JVM)

lazy val server_scalaJS: Project = ServerScalaJsProject("scalaJS", "server/scalaJS") dependsOn server_sharedJS_JS
lazy val jsProjects = Seq(server_scalaJS)
lazy val server_sharedJS = SharedJsProject("server-sharedJS", "server/server-sharedJS")
lazy val server_sharedJS_JS = server_sharedJS.js
lazy val server_sharedJS_JVM = server_sharedJS.jvm

lazy val runServer: TaskKey[Unit] = TaskKey[Unit]("runServer", "Run server.")
runServer := {}
runServer := (runServer dependsOn ((run in Compile) in web).toTask("")).value

lazy val buildServer: TaskKey[Unit] = TaskKey[Unit]("buildServer", "Build server.")
buildServer := (stage in web).value

lazy val runServerPackage: TaskKey[Unit] = TaskKey[Unit]("runServerPackage", "Run server package.")
runServerPackage := {
  implicit val path = baseDirectory.value / "server" / "web" / "target" / "universal" / "stage" / "bin"
  if (runScript("web") != 0) throw new Exception("Run server package crashed.")
}
runServerPackage := (runServerPackage dependsOn buildServer).value

// NodeJS-Proxy
val proxyPath = file("nodeJS-proxy")

lazy val proxyNpmInstall: TaskKey[Unit] = TaskKey[Unit]("proxyNpmInstall", "Install proxy node_modules")
proxyNpmInstall := {
  implicit val path = proxyPath
  if (runScript("npm install") != 0) throw new Exception("Install proxy node_modules crashed.")
}

lazy val runProxy: TaskKey[Unit] = TaskKey[Unit]("runProxy", "Run proxy.")
runProxy := {
  implicit val path = proxyPath
  if (runScript("npm start") != 0) throw new Exception("Run proxy crashed.")
}
runProxy := (runProxy dependsOn proxyNpmInstall).value

lazy val buildProxy: TaskKey[Unit] = TaskKey[Unit]("buildProxy", "Build proxy.")
buildProxy := {
  implicit val path = proxyPath
  if (runScript("npm run build:prod") != 0) throw new Exception("Build proxy crashed.")
}
buildProxy := (buildProxy dependsOn proxyNpmInstall).value

lazy val runProxyPackage: TaskKey[Unit] = TaskKey[Unit]("runProxyPackage", "Run proxy package.")
runProxyPackage := {
  implicit val path = proxyPath / "dist"
  if (runScript("node proxyService.js") != 0) throw new Exception("Run proxy package crashed.")
}
runProxyPackage := (runProxyPackage dependsOn buildProxy).value

// Web client
val webClientPath = file("web-client")

lazy val web_scalaJS: Project = ClientScalaJSProject("web-scalaJS", "web-client/web-scalaJS")
  .settings(
    crossTarget in(Compile, fastOptJS) := webClientPath / "src" / "app" / "scalajs",
    crossTarget in(Compile, fullOptJS) := webClientPath / "src" / "app" / "scalajs"
  )

lazy val webClientNpmInstall: TaskKey[Unit] = TaskKey[Unit]("webClientNpmInstall", "Install web-client node_modules")
webClientNpmInstall := {
  implicit val path = webClientPath
  if (runScript("npm install") != 0) throw new Exception("Install web-client node_modules crashed.")
}

lazy val runWebClient: TaskKey[Unit] = TaskKey[Unit]("runWebClient", "Run web client.")
runWebClient := {
  implicit val path = webClientPath
  if (runScript("ng serve") != 0) throw new Exception("Run web client crashed.")
}
runWebClient := (runWebClient dependsOn (fastOptJS in (web_scalaJS, Compile), webClientNpmInstall)).value

lazy val buildWebClient: TaskKey[Unit] = TaskKey[Unit]("buildWebClient", "Build web-client.")
buildWebClient := {
  implicit val path = webClientPath
  if (runScript("ng build --target=production --environment=prod") != 0) throw new Exception("Build web-client crashed.")
}
buildWebClient := (buildWebClient dependsOn (fullOptJS in (web_scalaJS, Compile), webClientNpmInstall)).value

// Desktop client and app
val desktopClientPath = file("desktop-client")

lazy val desktop_scalaJS: Project = ClientScalaJSProject("desktop-scalaJS", "desktop-client/desktop-scalaJS")
  .settings(
    crossTarget in(Compile, fastOptJS) := desktopClientPath / "src" / "app" / "scalajs",
    crossTarget in(Compile, fullOptJS) := desktopClientPath / "src" / "app" / "scalajs"
  )

lazy val desktopClientNpmInstall: TaskKey[Unit] = TaskKey[Unit]("desktopClientNpmInstall", "Install desktop-client node_modules")
desktopClientNpmInstall := {
  implicit val path = desktopClientPath
  if (runScript("npm install") != 0) throw new Exception("Install desktop-client node_modules crashed.")
}

lazy val runDesktopClient: TaskKey[Unit] = TaskKey[Unit]("runDesktopClient", "Run desktop client.")
runDesktopClient := {
  implicit val path = desktopClientPath
  if (runScript("ng serve") != 0) throw new Exception("Run desktop client crashed.")
}
runDesktopClient := runDesktopClient.dependsOn(fastOptJS in (desktop_scalaJS, Compile), desktopClientNpmInstall).value

lazy val runDesktopApp: TaskKey[Unit] = TaskKey[Unit]("runDesktopApp", "Run desktop application.")
runDesktopApp := {
  implicit val path = desktopClientPath
  if (runScript("electron .") != 0) throw new Exception("Run desktop application crashed.")
}

lazy val buildDesktopClient: TaskKey[Unit] = TaskKey[Unit]("buildDesktopClient", "Build desktop-client.")
buildDesktopClient := {
  implicit val path = desktopClientPath
  if (runScript("ng build --target=production --environment=prod") != 0) throw new Exception("Build desktop-client crashed.")
}
buildDesktopClient := (buildDesktopClient dependsOn (fullOptJS in (desktop_scalaJS, Compile), desktopClientNpmInstall)).value

lazy val buildDesktopApp: TaskKey[Unit] = TaskKey[Unit]("buildDesktopApp", "Build desktop application.")
buildDesktopApp := {
  implicit val path = desktopClientPath
  if (runScript("npm run build") != 0) throw new Exception("Build desktop application crashed.")
}
buildDesktopApp := (buildDesktopApp dependsOn desktopClientNpmInstall).value

// Mobile client
val mobileClientPath = file("mobile-client")

lazy val mobile_scalaJS: Project = ClientScalaJSProject("mobile-scalaJS", "mobile-client/mobile-scalaJS")
  .settings(
    crossTarget in(Compile, fastOptJS) := mobileClientPath / "app" / "services" / "scalaJS",
    crossTarget in(Compile, fullOptJS) := mobileClientPath / "app" / "services" / "scalaJS"
  )

lazy val mobileClientNpmInstall: TaskKey[Unit] = TaskKey[Unit]("mobileClientNpmInstall", "Install mobile-client node_modules")
mobileClientNpmInstall := {
  implicit val path = mobileClientPath
  if (runScript("npm install") != 0) throw new Exception("Install mobile-client node_modules crashed.")
}

lazy val runAndroidMobileClient: TaskKey[Unit] = TaskKey[Unit]("runAndroidMobileClient", "Run android mobile client.")
runAndroidMobileClient := {
  implicit val path = mobileClientPath
  if (runScript("react-native run-android") != 0) throw new Exception("Run android mobile client crashed.")
}
runAndroidMobileClient := runAndroidMobileClient.dependsOn(fastOptJS in (mobile_scalaJS, Compile), mobileClientNpmInstall).value

lazy val buildAndroidMobileClient: TaskKey[Unit] = TaskKey[Unit]("buildAndroidMobileClient", "Build android mobile client.")
buildAndroidMobileClient := {
  implicit val path = mobileClientPath / "android"
  if (runScript("gradlew assembleRelease") != 0) throw new Exception("Build android mobile client crashed.")
}
buildAndroidMobileClient := buildAndroidMobileClient.dependsOn(fullOptJS in (mobile_scalaJS, Compile), mobileClientNpmInstall).value

lazy val runIosMobileClient: TaskKey[Unit] = TaskKey[Unit]("runIosMobileClient", "Run iOS mobile client.")
runIosMobileClient := {
  implicit val path = mobileClientPath
  if (runScript("react-native run-ios") != 0) throw new Exception("Run iOS mobile client crashed.")
}
runIosMobileClient := runIosMobileClient.dependsOn(fastOptJS in (mobile_scalaJS, Compile), mobileClientNpmInstall).value

lazy val buildIosMobileClient: TaskKey[Unit] = TaskKey[Unit]("buildIosMobileClient", "Build iOS mobile client.")
buildIosMobileClient := {
  implicit val path = mobileClientPath
  if (runScript("react-native run-ios --configuration Release") != 0) throw new Exception("Build iOS mobile client crashed.")
}
buildIosMobileClient := buildIosMobileClient.dependsOn(fullOptJS in (mobile_scalaJS, Compile), mobileClientNpmInstall).value

lazy val runAll = TaskKey[Unit]("runAll", "Run all.")
runAll := {
  val server = runServer.value
  val proxy = runProxy.value
  val web = runWebClient.value
  val desktop = runDesktopClient.value
  val electron = runDesktopApp.value
  val android = runAndroidMobileClient.value
  val ios = if (!isWindows) runIosMobileClient.value
  ()
}

lazy val buildAll = TaskKey[Unit]("buildAll", "Build all.")
buildAll := {
  val server = buildServer.value
  val proxy = buildProxy.value
  val webClient = buildWebClient.value
  val desktopClient = buildDesktopClient.value
  val desktopApp = buildDesktopApp.value
  val androidApp = buildAndroidMobileClient.value
  val iosApp = if (!isWindows) buildIosMobileClient.value
  ()
}

(compile in Compile) := {
  (compile in Compile) dependsOn (
    fastOptJS in (server_scalaJS, Compile),
    fastOptJS in (web_scalaJS, Compile),
    fastOptJS in (desktop_scalaJS, Compile),
    fastOptJS in (mobile_scalaJS, Compile),
    proxyNpmInstall,
    webClientNpmInstall,
    desktopClientNpmInstall
  )
}.value