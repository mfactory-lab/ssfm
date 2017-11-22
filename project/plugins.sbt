// snapshot repository
resolvers += "Sonatype OSS Snapshot Repository" at "https://oss.sonatype.org/content/repositories/snapshots/"

addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.1")

// SBT Native Packager
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.2.0")

// updates plugin
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.3.0")
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.8.2")

// updates plugin
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.3.0")
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.8.2")

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.13")

//addSbtPlugin("com.typesafe.sbt" % "sbt-mocha" % "1.1.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-gzip" % "1.0.0")

// scala JS
addSbtPlugin("com.vmunier" % "sbt-web-scalajs" % "1.0.3")
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.15")

// To get build info in the app
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.6.1")

// Heroku
addSbtPlugin("com.heroku" % "sbt-heroku" % "1.0.1")

// Ensime
addSbtPlugin("org.ensime" % "sbt-ensime" % "1.12.9")