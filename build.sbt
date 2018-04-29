def moduleName(name: String) = s"itunes-dap-sync-$name"

lazy val commonSettings = Seq(
  organization := "guilgaly",
  version := "0.0.1-SNAPSHOT",
  scalaVersion := Deps.scalaVersion,
  scalafmtOnCompile := true,
  resolvers ++= Deps.customResolvers,
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
      Deps.scopt,
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
      Deps.jaudiotagger,
      Deps.vlcj,
      Deps.jave,
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
      Deps.ddPlist,
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
      Deps.logging.log4s,
      Deps.logging.slf4jApi,
      Deps.logging.slf4jSimple,
      Deps.upickle,
      Deps.scalatest % "test",
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
