organization in ThisBuild := "play-jobs"

inThisBuild(
  List(
    scalaVersion := "2.13.14"
  )
)

name := """play-jobs"""

buildInfoPackage := "buildInfo"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala,
                 JavaAgent,
                 SbtWeb,
                 UniversalDeployPlugin,
                 BuildInfoPlugin)
  .settings(Settings.commonPlayFront: _*)
  .settings(
    libraryDependencies ++= Seq(guice,
                                openId,
                                ehcache,
                                jdbc,
                                evolutions,
                                filters))
  .settings(routesGenerator := InjectedRoutesGenerator)

ThisBuild / evictionErrorLevel := Level.Info

scapegoatVersion in ThisBuild := "1.3.11"

resolvers += Resolver.sbtPluginRepo("releases")

resolvers += Resolver.jcenterRepo // Adds Bi
