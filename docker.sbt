maintainer in Docker := "Yassine KHILAJI <ykhilaji@gmail.com>"
packageSummary in Docker := "Play jobs"

dockerBaseImage := "openjdk:8-jre-alpine"

// Don't pull from dockerhub when building inside TRAVIS-CI
dockerRepository := (if (sys.env.get("TRAVIS").isEmpty) Some("ykhilaji")
                     else None)

// TODO: revert once https://github.com/sbt/sbt-native-packager/issues/1202 is fixed
daemonUserUid in Docker := None
daemonUser in Docker := "daemon"

dockerUpdateLatest := true

enablePlugins(AshScriptPlugin)

dockerExposedPorts := Seq(9000)

dockerImageCreationTask := (publishLocal in Docker).value

variablesForSubstitution := Map("VERSION" -> version.value)
