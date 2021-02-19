import Dependencies._
import sbt.Keys.test
import sbt._

ThisBuild / scalaVersion := Versions.scalaVersion
ThisBuild / crossScalaVersions := Seq(Versions.scalaVersion)

ThisBuild / homepage := Some(url("https://orahub.oci.oraclecorp.com/harish_butani/spark-oracle"))
ThisBuild / licenses := List("Apache 2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / organization := "org.rhbutani"
ThisBuild / version := Versions.sparksqlMacrosVersion

// from https://www.scala-sbt.org/1.x/docs/Cached-Resolution.html
// added to commonSettings
// ThisBuild / updateOptions := updateOptions.value.withLatestSnapshots(false)
// ThisBuild / updateOptions := updateOptions.value.withCachedResolution(true)

Global / resolvers ++= Seq(
  DefaultMavenRepository,
  Resolver.sonatypeRepo("public"),
  "Apache snapshots repo" at "https://repository.apache.org/content/groups/snapshots/")

lazy val commonSettings = Seq(
  updateOptions := updateOptions.value.withLatestSnapshots(false),
  updateOptions := updateOptions.value.withCachedResolution(true),
  javaOptions := Seq(
    "-Xms1g",
    "-Xmx3g",
    "-Duser.timezone=UTC",
    "-Dscalac.patmat.analysisBudget=512",
    "-XX:MaxPermSize=256M",
    "-Xrunjdwp:transport=dt_socket,address=5005,server=y,suspend=n"),
  scalacOptions ++= Seq("-target:jvm-1.8", "-feature", "-deprecation"),
  licenses := Seq("Apache License, Version 2.0" ->
    url("http://www.apache.org/licenses/LICENSE-2.0")
  ),
  homepage := Some(url("https://github.com/hbutani/spark-sql-macros")),
  test in assembly := {},
  fork in Test := true,
  parallelExecution in Test := false,
  libraryDependencies ++= (scala.dependencies ++
    spark.dependencies ++
    utils.dependencies ++
    test_infra.dependencies),
  excludeDependencies ++= Seq(ExclusionRule("org.apache.calcite.avatica"))
)

lazy val macros = project
  .in(file("macros"))
  .disablePlugins(AssemblyPlugin)
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= scala.dependencies)

lazy val sql = project
  .in(file("sql"))
  .aggregate(macros)
  .settings(commonSettings: _*)
  .settings(Assembly.assemblySettings: _*)
  .settings(
    name := "spark-sql-macros",
    assemblyJarName in assembly := s"${name.value}_${scalaVersion.value}_${version.value}.jar"
  ).
  dependsOn(macros % "compile->compile;test->test")

  
