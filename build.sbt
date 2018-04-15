import com.lucidchart.sbt.scalafmt.ScalafmtCorePlugin.autoImport.ignoreErrors
import sbt.Keys.organization

def moduleName(name: String) = s"itunes-dap-sync-$name"

val akkaVersion = "2.5.11"
val slf4jVersion = "1.7.25"

lazy val commonSettings = Seq(
  organization := "guilgaly",
  version := "0.0.1-SNAPSHOT",
  scalaVersion := "2.12.5",
  scalafmtVersion := "1.4.0",
  scalafmtOnCompile := true,
  ignoreErrors in scalafmt := false,
  resolvers ++= Seq(
    Resolver.bintrayRepo("ijabz", "maven"),
    Resolver.bintrayRepo("guilgaly", "maven")
  ),
)

lazy val root = (project in file("."))
  .aggregate(cmdLineApp, guiApp, core, itunesLib, common)

lazy val cmdLineApp = (project in file("app-cmdline"))
  .dependsOn(
    core,
    common % "compile->compile;test->test",
  )
  .settings(
    name := moduleName("cmdLineApp"),
    commonSettings,
    libraryDependencies ++= Seq(
      // Parsing command-line args
      "com.github.scopt" %% "scopt" % "3.7.0",
    )
  )

lazy val guiApp = (project in file("app-gui"))
  .dependsOn(
    core,
    common % "compile->compile;test->test",
  )
  .settings(
    name := moduleName("guiApp"),
    commonSettings,
    libraryDependencies ++= Seq(
    )
  )

lazy val core = (project in file("core"))
  .dependsOn(
    itunesLib,
    common % "compile->compile;test->test",
  )
  .settings(
    name := moduleName("core"),
    commonSettings,
    libraryDependencies ++= Seq(
      // Audio files tags
      "net.jthink" % "jaudiotagger" % "2.2.5",
      // Audio files manipulation (transcoding...)
      "uk.co.caprica" % "vlcj" % "3.10.1",
      "it.sauronsoftware" % "jave" % "1.0.2",
    )
  )

lazy val itunesLib = (project in file("ituneslib"))
  .dependsOn(
    common % "compile->compile;test->test"
  )
  .settings(
    name := moduleName("itunesLib"),
    commonSettings,
    libraryDependencies ++= Seq(
      // Plist parser
      "com.googlecode.plist" % "dd-plist" % "1.20",
    )
  )

lazy val experiments = (project in file("experiments"))
  .dependsOn(
    core,
    common % "compile->compile;test->test",
  )
  .settings(
    name := moduleName("experiments"),
    commonSettings,
    libraryDependencies ++= Seq(
    )
  )

lazy val common = (project in file("common"))
  .settings(
    name := moduleName("common"),
    commonSettings,
    libraryDependencies ++= Seq(
        // Logging
        "org.log4s" %% "log4s" % "1.6.1",
        "org.slf4j" % "slf4j-api" % slf4jVersion,
        "org.slf4j" % "slf4j-simple" % slf4jVersion, // logging to System.err for now
        // JSON
        "com.lihaoyi" %% "upickle" % "0.6.5",
        // Tests
        "org.scalatest" %% "scalatest" % "3.0.5" % "test",
    )
  )

//libraryDependencies ++= Seq(
//  // Akka
//  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
//  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
//  // Tests
//  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
//
//  // ImageIO plugins (improve album art support)
//  "com.twelvemonkeys.imageio" % "imageio-jpeg" % "3.3.2",
//  "com.twelvemonkeys.imageio" % "imageio-core" % "3.3.2",
//  "com.twelvemonkeys.common" % "common-lang" % "3.3.2",
//  // Others
//  "org.scala-lang.modules" %% "scala-java8-compat" % "0.8.0"
//)
