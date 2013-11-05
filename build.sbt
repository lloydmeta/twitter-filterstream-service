name := "twitter-filterstream"

version := "1.0-SNAPSHOT"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

resolvers += "Sonatype Nexus Releases" at "https://oss.sonatype.org/content/repositories/releases"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  "com.beachape.twitterFilterStream" %% "twitter-filterstream" % "0.0.1-SNAPSHOT",
  cache
)

play.Project.playScalaSettings
