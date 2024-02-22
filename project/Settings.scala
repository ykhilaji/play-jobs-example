import com.lucidchart.sbt.scalafmt.ScalafmtCorePlugin.autoImport._
import com.sksamuel.scapegoat.sbt.ScapegoatSbtPlugin.autoImport._
import sbt._
import sbt.Keys._
import com.typesafe.sbt.digest.SbtDigest.autoImport._
import com.typesafe.sbt.gzip.SbtGzip.autoImport._
import com.typesafe.sbt.packager.Keys._
import com.typesafe.sbt.packager.universal.UniversalPlugin.autoImport._
import com.typesafe.sbt.web.SbtWeb.autoImport._
import play.sbt.PlayImport.PlayKeys
import play.sbt.routes.RoutesKeys
import play.twirl.sbt.Import.TwirlKeys
import sbt.internal.io.Source
import sbtbuildinfo.BuildInfoPlugin.autoImport._
import sbtrelease.ReleasePlugin.autoImport._
import com.typesafe.sbt.packager.SettingsHelper._
import ReleaseTransformations._

object Settings {
  val timestamp = new java.text.SimpleDateFormat("yyyyMMdd-HHmm").format(new java.util.Date())


  lazy val common = Seq(
    scalacOptions := Seq(
      "-encoding",
      "UTF-8",
      "-target:jvm-1.8",
      "-feature",
      "-deprecation",
      "-language:postfixOps",
      "-Ywarn-dead-code"
    ),
    scalacOptions += "-P:semanticdb:synthetics:on",

      // name dist with timestamp
    packageName in Universal := s"${name.value}-${version.value}-$timestamp",
    // skip scaladoc when running dist
    publishArtifact in (Compile, packageDoc) := false,
    publishArtifact in packageDoc := false,
    sources in (Compile, doc) := Seq.empty
  ) ++ scapegoatSettings ++ scalaFmtSettings

  lazy val commonPlay = common ++ Seq(
    buildInfoPackage := "sbt",
    buildInfoKeys := Seq[BuildInfoKey](
      name,
      version,
      scalaVersion,
      BuildInfoKey.constant("buildTime" -> timestamp)
    ),
   

    evictionWarningOptions in update := EvictionWarningOptions.default
    .withWarnTransitiveEvictions(false)
    .withWarnDirectEvictions(false),
    
    pipelineStages := Seq(digest),

    excludeFilter in digest := "*.zip" || "*.exe",

    // dont include local.conf in dist
    mappings in Universal := {
      val origMappings = (mappings in Universal).value
      origMappings.filterNot { case (_, file) => file.endsWith("local.conf") }
    },
    libraryDependencies ++= Seq(
     
      Dependencies.play.json,
      Dependencies.play.json_joda,
    /*   Dependencies.play.iteratees,
      Dependencies.play.reactive_streams, */
   //   Dependencies.play.request_tracer,
      Dependencies.play.json_extensions,

      Dependencies.akka.kryo_serialization,
      Dependencies.akka.actor,
      Dependencies.akka.slf4j,
      Dependencies.akka.cluster,
      Dependencies.akka.cluster_tools,
      Dependencies.akka.stream,
      Dependencies.akka.jackson,
      Dependencies.akka.akka_typed,
      Dependencies.akka.akka_cluster_typed,
     // Dependencies.akka.coordination,
     // Dependencies.akka.remote,
     // Dependencies.akka.protobuf,
    //  Dependencies.akka.pki,

      Dependencies.circuitBreaker.core,

      Dependencies.db.anorme,
      Dependencies.enum.core,
      Dependencies.opencvs.core,

      Dependencies.scalatest.play,

      Dependencies.jsMessages.core,

      Dependencies.posgtres.driver,

      Dependencies.pdfbox.core,

      Dependencies.scalaz.core,

      Dependencies.HikariCP.core,

      Dependencies.commonIo.core,

      Dependencies.kamon.bundele,
      Dependencies.kamon.prometheus,

      Dependencies.mockito.core % Test,
      Dependencies.akka.testkit % Test
    ),

    releaseProcess := releaseSteps
  )

  lazy val commonPlayFront = commonPlay ++ Seq(
    pipelineStages := Seq(digest, gzip),
    includeFilter in digest := "*.js" || "*.css" || "*.html",
    includeFilter in gzip := "*.js" || "*.css" || "*.html",
    libraryDependencies ++= Seq()
  )

  lazy val releaseSteps = {
    Seq[ReleaseStep](
      runClean,
      checkSnapshotDependencies,
      inquireVersions,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      releaseStepTask(publish in Universal),
      setNextVersion,
      commitNextVersion,
      pushChanges
    )
  }

  lazy val scapegoatSettings = Seq(
    scapegoatVersion := "1.3.3",
    scapegoatReports := Seq("xml", "html"),
    scapegoatConsoleOutput := false
  )

  lazy val scalaFmtSettings = Seq(
    scalafmtVersion := "1.3.0",
    scalafmtOnCompile := true
  )

  def getSourceFile(source: Source): java.io.File = {
    val cl = classOf[Source]
    val baseField = cl.getDeclaredField("base")
    baseField.setAccessible(true)
    baseField.get(source).asInstanceOf[java.io.File]
  }
}
