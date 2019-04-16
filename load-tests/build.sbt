name := "load-tests"

version := "1.0"

scalaVersion := "2.12.8"

libraryDependencies ++= {
  val faunaDbVersion = "2.6.0"
  val scalaTestVersion = "3.0.5"
  val junitVersion = "4.12"
  val jmeterVersion = "5.1.1"
  val excludeJacksonRule = ExclusionRule(organization = "com.fasterxml.jackson.core")
  
  Seq(
    "com.faunadb" %% "faunadb-scala" % faunaDbVersion,
    "org.scalactic" %% "scalactic" % scalaTestVersion,
    "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
    "junit" % "junit" % junitVersion % "test",
    "org.apache.jmeter" % "ApacheJMeter_junit" % jmeterVersion excludeAll(excludeJacksonRule)
  )
}

enablePlugins(PackPlugin)

packCopyDependenciesTarget := crossTarget.value / "lib"

packCopyDependenciesUseSymbolicLinks := false

commands += Command.command("package-all") { state =>
  "package" ::
  "test:package" ::
  "packCopyDependencies" ::
  state
}