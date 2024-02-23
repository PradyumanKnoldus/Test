ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .settings(
    name := "Test"
  )

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "3.5.0",
  "org.apache.spark" %% "spark-sql" % "3.5.0",
  "org.apache.spark" %% "spark-streaming" % "3.5.0",
  "org.apache.kafka" % "kafka-clients" % "3.6.0",
  "org.apache.spark" %% "spark-sql-kafka-0-10" % "3.5.0",
  "org.scalaj" %% "scalaj-http" % "2.4.2",
//  "org.json4s" %% "json4s-jackson" % "4.0.7",
//  "org.json4s" %% "json4s-native" % "3.6.11",
  "com.typesafe.akka" %% "akka-http" % "10.5.3",
  "com.typesafe.akka" %% "akka-stream" % "2.8.5",
  "io.circe" %% "circe-core" % "0.14.1",
  "io.circe" %% "circe-generic" % "0.14.1",
  "io.circe" %% "circe-parser" % "0.14.1"
)