name := """epacs_admin"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.2"

libraryDependencies += jdbc
libraryDependencies += guice
libraryDependencies += javaJdbc
libraryDependencies += javaWs
libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.41"
libraryDependencies ++= Seq(
  javaJpa,
  "org.hibernate" % "hibernate-entitymanager" % "5.1.0.Final"
)
libraryDependencies += "commons-io" % "commons-io" % "2.5"

libraryDependencies ++= Seq(
  "com.adrianhurt" %% "play-bootstrap" % "1.2-P26-B3"
)
libraryDependencies += "com.amazonaws" % "aws-java-sdk" % "1.9.2"

// Testing libraries for dealing with CompletionStage...
libraryDependencies += "org.assertj" % "assertj-core" % "3.6.2" % Test
libraryDependencies += "org.awaitility" % "awaitility" % "2.0.0" % Test
// Make verbose tests
testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))
PlayKeys.externalizeResources := false