resolvers += "cm" at "http://maven.codemettle.com/repository/internal"

addSbtPlugin("com.thesamet" % "sbt-protoc" % "0.99.3-JAVA6")

libraryDependencies += "com.trueaccord.scalapb" %% "compilerplugin" % "0.5.47-JAVA6"
