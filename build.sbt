name := "text-analyzer-scala-js"

version := "1.0"

scalaVersion := "2.11.5"

enablePlugins(ScalaJSPlugin)

libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.8.0"

libraryDependencies += "be.doeraene" %%% "scalajs-jquery" % "0.8.0"

skip in packageJSDependencies := false

persistLauncher in Compile := true

persistLauncher in Test := false
