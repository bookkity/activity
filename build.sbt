name := "Activity"
version := "0.1"
scalaVersion := "2.13.4"

resolvers += Resolver.JCenterRepository
resolvers += "panda-repository" at "https://repo.panda-lang.org/"

libraryDependencies += "net.katsstuff" %% "ackcord"          % "0.17.1" // For high level API, includes all the other modules
libraryDependencies += "net.katsstuff" %% "ackcord-core"     % "0.17.1" // Low level core API
libraryDependencies += "net.katsstuff" %% "ackcord-commands" % "0.17.1" // Commands API

libraryDependencies += "net.dzikoysk" % "cdn" % "1.5.3" excludeAll(
  ExclusionRule(organization = "org.tinylog")
)

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"

assemblyMergeStrategy in assembly := {
  case "module-info.class" => MergeStrategy.discard
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}