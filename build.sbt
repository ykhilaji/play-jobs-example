organization in ThisBuild := "play-jobs"

inThisBuild(
  List(
    scalaVersion := "2.13.13"
  )
)

name := """play-jobs"""

buildInfoPackage := "buildInfo"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala, SbtWeb, UniversalDeployPlugin, BuildInfoPlugin)
  .settings(Settings.commonPlayFront: _*)
  .settings(
    libraryDependencies ++= Seq(guice,
                                ws,
                                openId,
                                ehcache,
                                jdbc,
                                evolutions,
                                filters))
  .settings(routesGenerator := InjectedRoutesGenerator)
  .settings(
    run / fork := true
  )

ThisBuild / evictionErrorLevel := Level.Info

scapegoatVersion in ThisBuild := "1.3.11"

resolvers += Resolver.sbtPluginRepo("releases")

resolvers += Resolver.jcenterRepo // Adds Bi
