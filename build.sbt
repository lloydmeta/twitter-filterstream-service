name := "twitter-filterstream"

version := "1.0-SNAPSHOT"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

resolvers += "Sonatype Nexus Releases" at "https://oss.sonatype.org/content/repositories/releases"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  "com.beachape.twitterFilterStream" %% "twitter-filterstream" % "0.0.1-SNAPSHOT",
  "com.newrelic.agent.java" % "newrelic-agent" % "3.1.1",
  "com.typesafe.akka" %% "akka-testkit" % "2.2.1" % "test",
  "org.scalatest" %% "scalatest" % "2.0" % "test",
  cache
)

play.Project.playScalaSettings
