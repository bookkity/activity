name := "Activity"
version := "0.1"
scalaVersion := "2.13.4"

resolvers += Resolver.JCenterRepository

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2"

libraryDependencies += "net.katsstuff" %% "ackcord"                 % "0.17.1" //For high level API, includes all the other modules
libraryDependencies += "net.katsstuff" %% "ackcord-core"            % "0.17.1" //Low level core API
libraryDependencies += "net.katsstuff" %% "ackcord-commands"        % "0.17.1" //Commands API
libraryDependencies += "net.katsstuff" %% "ackcord-lavaplayer-core" % "0.17.1" //Low level lavaplayer API