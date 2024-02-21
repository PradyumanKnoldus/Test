ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .settings(
    name := "Test"
  )

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "3.4.0",
  "org.apache.spark" %% "spark-sql" % "3.4.0",
  "org.apache.spark" %% "spark-streaming" % "3.4.0",
  "org.apache.kafka" % "kafka-clients" % "3.4.0",
  "org.apache.spark" %% "spark-sql-kafka-0-10" % "3.3.2",
  "io.delta" %% "delta-core" % "2.2.0"

)