ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.8"
ThisBuild / dynverSeparator := "-"

Compile / scalacOptions ++= Seq(
  "-target:11",
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xlog-reflective-calls",
  "-Xlint")
Compile / javacOptions ++= Seq("-Xlint:unchecked", "-Xlint:deprecation")


val AkkaVersion = "2.6.19"
val AkkaHttpVersion = "10.2.9"
val AkkaManagementVersion = "1.1.3"
val AkkaPersistenceJdbcVersion = "5.0.4"
val AlpakkaKafkaVersion = "2.0.7"
val AkkaProjectionVersion = "1.2.4"
val ScalikeJdbcVersion = "3.5.0"


enablePlugins(AkkaGrpcPlugin)
enablePlugins(JavaAppPackaging, DockerPlugin)

dockerBaseImage := "docker.io/library/adoptopenjdk:11-jre-hotspot"
dockerUsername := sys.props.get("docker.username")
dockerRepository := sys.props.get("docker.registry")

// Common dependencies for logging and testing
lazy val loggingAndTestingDependencies = Seq(
  "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.9",
  "org.scalatest" %% "scalatest" % "3.1.2" % Test,
)

// Akka Management powers Health Checks and Akka Cluster Bootstrapping
lazy val managementDependencies = Seq(
  "com.lightbend.akka.management" %% "akka-management" % AkkaManagementVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
  "com.lightbend.akka.management" %% "akka-management-cluster-http" % AkkaManagementVersion,
  "com.lightbend.akka.management" %% "akka-management-cluster-bootstrap" % AkkaManagementVersion,
  "com.lightbend.akka.discovery" %% "akka-discovery-kubernetes-api" % AkkaManagementVersion,
  "com.typesafe.akka" %% "akka-discovery" % AkkaVersion
)

// 1. Basic dependencies for a clustered application
lazy val clusterDependencies = Seq(
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-cluster-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-cluster-sharding-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion % Test,
  "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % Test
)

// 2. Using gRPC and/or protobuf
lazy val gRPCDependencies = Seq(
  "com.typesafe.akka" %% "akka-http2-support" % AkkaHttpVersion
)

// 3. Using Akka Persistence
lazy val akkaPersistence = Seq(
  "com.typesafe.akka" %% "akka-persistence-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-serialization-jackson" % AkkaVersion,
  "com.lightbend.akka" %% "akka-persistence-jdbc" % AkkaPersistenceJdbcVersion,
  "com.typesafe.akka" %% "akka-persistence-testkit" % AkkaVersion % Test,
  "org.postgresql" % "postgresql" % "42.2.18"
)

// 4. Querying or projecting data from Akka Persistence
lazy val queryingAndProjectingDependencies = Seq(
  "com.typesafe.akka" %% "akka-persistence-query" % AkkaVersion,
  "com.lightbend.akka" %% "akka-projection-eventsourced" % AkkaProjectionVersion,
  "com.lightbend.akka" %% "akka-projection-jdbc" % AkkaProjectionVersion,
  "org.scalikejdbc" %% "scalikejdbc" % ScalikeJdbcVersion,
  "org.scalikejdbc" %% "scalikejdbc-config" % ScalikeJdbcVersion,
  "com.typesafe.akka" %% "akka-stream-kafka" % AlpakkaKafkaVersion,
  "com.lightbend.akka" %% "akka-projection-testkit" % AkkaProjectionVersion % Test
)

lazy val root = (project in file("."))
  .settings(
    name := "AkkaMicroservices",
    libraryDependencies ++= loggingAndTestingDependencies,
    libraryDependencies ++= managementDependencies,
    libraryDependencies ++= clusterDependencies,
    libraryDependencies ++= gRPCDependencies,
    libraryDependencies ++= akkaPersistence,
    libraryDependencies ++= queryingAndProjectingDependencies
  )
