name := "xchange"

version := "4.3.5"

scalaVersion := "2.12.3"

resolvers += Resolver.jcenterRepo

val dependencies = Seq(
    "org.apache.commons" % "commons-lang3" % "3.7",
    "javax.ws.rs" % "javax.ws.rs-api" % "2.1" artifacts( Artifact("javax.ws.rs-api", "jar", "jar")),
    "com.github.mmazi" % "rescu" % "2.0.2" exclude("javax.ws.rs", "javax.ws.rs-api"),
    "commons-io" % "commons-io" % "2.6"
)

lazy val `xchange-core` = (project in file("xchange-core")).settings(
    libraryDependencies ++= dependencies
)

lazy val `xchange-binance` = (project in file("xchange-binance")).dependsOn(`xchange-core`)
lazy val `xchange-hitbtc` = (project in file("xchange-hitbtc")).dependsOn(`xchange-core`)

lazy val `xchange` = (project in file(".")).dependsOn(
    `xchange-core`,
    `xchange-binance`,
    `xchange-hitbtc`
)

sources in (Compile, doc) := Seq.empty
publishArtifact in (Compile, packageDoc) := false