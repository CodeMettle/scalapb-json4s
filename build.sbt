import ReleaseTransformations._

scalaVersion := "2.11.7"

crossScalaVersions := Seq("2.10.5", "2.11.7")

organization in ThisBuild := "com.trueaccord.scalapb"

name in ThisBuild := "scalapb-json4s"

scalacOptions in ThisBuild ++= {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, v)) if v <= 11 => List("-target:jvm-1.6")
    case _ => Nil
  }
}

resolvers ++= Seq(
  "cm" at "http://maven.codemettle.com/repository/internal",
  "cm/snaps" at "http://maven.codemettle.com/repository/snapshots"
)

publishMavenStyle in ThisBuild := true

credentials += {
  def file = "credentials-" + (if (isSnapshot.value) "snapshots" else "internal")

  Credentials(Path.userHome / ".m2" / file)
}

publishTo := {
  def path = "/repository/" + (if (isSnapshot.value) "snapshots" else "internal")

  Some("CodeMettle Maven" at s"http://maven.codemettle.com$path")
}

releaseCrossBuild := true

releasePublishArtifactsAction := PgpKeys.publishSigned.value

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  ReleaseStep(action = Command.process("publishSigned", _), enableCrossBuild = true),
  setNextVersion,
  commitNextVersion,
  pushChanges,
  ReleaseStep(action = Command.process("sonatypeReleaseAll", _), enableCrossBuild = true)
)

val scalaPbVersion = "0.5.47-JAVA6"

libraryDependencies ++= Seq(
  "com.trueaccord.scalapb" %% "scalapb-runtime" % scalaPbVersion,
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.7.9",
  "org.json4s" %% "json4s-jackson" % "3.5.0" exclude("com.fasterxml.jackson.core", "jackson-databind"),
  "org.scalatest" %% "scalatest" % "3.0.0" % "test",
  "com.google.protobuf" % "protobuf-java-util" % "3.1.0" % "test",
  "com.google.protobuf" % "protobuf-java" % "3.1.0" % "protobuf"
)

Project.inConfig(Test)(sbtprotoc.ProtocPlugin.protobufConfigSettings)

PB.targets in Compile := Nil

PB.targets in Test := Seq(
  PB.gens.java -> (sourceManaged in Test).value,
  scalapb.gen(javaConversions=true) -> (sourceManaged in Test).value
)
