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
    sources in (Compile, doc) := Seq.empty,
    publishArtifact in (Compile, packageDoc) := false,
    libraryDependencies ++= dependencies
)

lazy val `xchange-binance` = (project in file("xchange-binance")).settings(
    sources in (Compile, doc) := Seq.empty,
    publishArtifact in (Compile, packageDoc) := false
).dependsOn(`xchange-core`)

lazy val `xchange-hitbtc` = (project in file("xchange-hitbtc")).settings(
    sources in (Compile, doc) := Seq.empty,
    publishArtifact in (Compile, packageDoc) := false
).dependsOn(`xchange-core`)

lazy val `xchange-bitstamp` = (project in file("xchange-bitstamp")).settings(
    sources in (Compile, doc) := Seq.empty,
    publishArtifact in (Compile, packageDoc) := false
).dependsOn(`xchange-core`)

lazy val `xchange-bittrex` = (project in file("xchange-bittrex")).settings(
    sources in (Compile, doc) := Seq.empty,
    publishArtifact in (Compile, packageDoc) := false
).dependsOn(`xchange-core`)

lazy val `xchange-bitfinex` = (project in file("xchange-bitfinex")).settings(
    sources in (Compile, doc) := Seq.empty,
    publishArtifact in (Compile, packageDoc) := false
).dependsOn(`xchange-core`)

lazy val `xchange-poloniex` = (project in file("xchange-poloniex")).settings(
    sources in (Compile, doc) := Seq.empty,
    publishArtifact in (Compile, packageDoc) := false
).dependsOn(`xchange-core`)

lazy val `xchange-huobi` = (project in file("xchange-huobi")).settings(
    sources in (Compile, doc) := Seq.empty,
    publishArtifact in (Compile, packageDoc) := false
).dependsOn(`xchange-core`)

lazy val `xchange-okcoin` = (project in file("xchange-okcoin")).settings(
    sources in (Compile, doc) := Seq.empty,
    publishArtifact in (Compile, packageDoc) := false
).dependsOn(`xchange-core`)

lazy val `xchange-kraken` = (project in file("xchange-kraken")).settings(
    sources in (Compile, doc) := Seq.empty,
    publishArtifact in (Compile, packageDoc) := false
).dependsOn(`xchange-core`)

lazy val `xchange` = (project in file(".")).dependsOn(
    `xchange-core`,
    `xchange-binance`,
    `xchange-hitbtc`,
    `xchange-bitfinex`,
    `xchange-bitstamp`,
    `xchange-bittrex`,
    `xchange-poloniex`,
    `xchange-huobi`,
    `xchange-okcoin`,
    `xchange-kraken`
)

sources in (Compile, doc) := Seq.empty
publishArtifact in (Compile, packageDoc) := false