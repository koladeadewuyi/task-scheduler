import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(Seq(
      organization := "com.example",
      scalaVersion := "2.12.6",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "task-scheduler",
    libraryDependencies ++= Seq(
      log4jApi,
      log4jApiScala,
      log4jCore,
      scalaTest % Test
    )
  )
