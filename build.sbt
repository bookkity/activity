name := "Activity"
version := "0.1"
scalaVersion := "2.13.4"

resolvers += Resolver.JCenterRepository
resolvers += "panda-repository" at "https://repo.panda-lang.org/"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2" // Logging implementation of SLF4J

libraryDependencies += "net.katsstuff" %% "ackcord"          % "0.17.1" // For high level API, includes all the other modules
libraryDependencies += "net.katsstuff" %% "ackcord-core"     % "0.17.1" // Low level core API
libraryDependencies += "net.katsstuff" %% "ackcord-commands" % "0.17.1" // Commands API

libraryDependencies += "net.dzikoysk" % "cdn" % "1.5.3" // CDN based storage
