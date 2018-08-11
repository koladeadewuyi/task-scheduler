import sbt._

object Dependencies {
  private val ScalaTestVersion = "3.0.5"
  private val Log4jScalaApiVersion = "11.0"
  private val Log4jVersion = "2.9.1"

  lazy val log4jApiScala = "org.apache.logging.log4j" %% "log4j-api-scala" % Log4jScalaApiVersion
  lazy val log4jApi = "org.apache.logging.log4j" % "log4j-api" % Log4jVersion
  lazy val log4jCore = "org.apache.logging.log4j" % "log4j-core" % Log4jVersion
  lazy val scalaTest = "org.scalatest" %% "scalatest" % ScalaTestVersion
}
