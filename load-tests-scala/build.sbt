name := "load-tests-scala"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "com.faunadb" %% "faunadb-scala" % "2.6.0",
  "org.scalactic" %% "scalactic" % "3.0.5",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  "junit" % "junit" % "4.12" % "test",
  "org.apache.jmeter" % "ApacheJMeter_junit" % "5.1.1"
)