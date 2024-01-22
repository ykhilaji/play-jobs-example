organization in ThisBuild := "play-jobs"
scalaVersion in ThisBuild := "2.12.11"

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

scapegoatVersion in ThisBuild := "1.3.3"

resolvers += Resolver.sbtPluginRepo("releases")

resolvers += Resolver.jcenterRepo // Adds Bi
