name := "s.avenge"

version := "2.0"

organization := "beep02.mean.things"

scalaVersion := "2.11.4"

libraryDependencies ++= Seq(
  "org.scala-lang"          % "scala-reflect"   % "2.11.4",
  "com.typesafe.akka"      %% "akka-actor"      % "2.3.4",
  "com.typesafe.akka"      %% "akka-slf4j"      % "2.3.4",
  "ch.qos.logback"          % "logback-classic" % "1.1.2",
  // JUnit is used for some Java interop. examples. A driver for JUnit:
  "junit"                   % "junit-dep"       % "4.10"   % "test",
  "com.novocode"            % "junit-interface" % "0.10"   % "test"
)

scalacOptions ++= Seq(
  "-encoding", "UTF-8", "-optimise",
  "-deprecation", "-unchecked", "-feature", "-Xlint",
  "-Ywarn-infer-any") // Nice, but hard to eliminate these warnings: "-Ywarn-value-discard")

javacOptions  ++= Seq(
  "-Xlint:unchecked", "-Xlint:deprecation") // Java 8: "-Xdiags:verbose")

// Enable improved incremental compilation feature in 2.11.X.
// see http://www.scala-lang.org/news/2.11.1
incOptions := incOptions.value.withNameHashing(true)
