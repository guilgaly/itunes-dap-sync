import sbt._
import sbt.librarymanagement.Resolver

object Deps {

  val customResolvers = Seq(
    Resolver.bintrayRepo("ijabz", "maven"),
    Resolver.bintrayRepo("guilgaly", "maven")
  )

  val scalaVersion = "2.12.6"

  /** Command-line args parsing. */
  val scopt = "com.github.scopt" %% "scopt" % "3.7.0"

  /** Audio files tags.*/
  val jaudiotagger = "net.jthink" % "jaudiotagger" % "2.2.5"

  /** Audio files manipulation (transcoding...) with VLC. */
  val vlcj = "uk.co.caprica" % "vlcj" % "3.10.1"

  /** Audio files manipulation (transcoding...) with ffmpeg. */
  val jave = "it.sauronsoftware" % "jave" % "1.0.2"

  /** Plist parser. */
  val ddPlist = "com.googlecode.plist" % "dd-plist" % "1.20"

  /** Logging. */
  object logging {
    private val slf4jVersion = "1.7.25"

    val log4s = "org.log4s" %% "log4s" % "1.6.1"
    val slf4jApi = "org.slf4j" % "slf4j-api" % slf4jVersion
    // logging to System.err for now
    val slf4jSimple = "org.slf4j" % "slf4j-simple" % slf4jVersion
  }

  /** JSON. */
  val upickle = "com.lihaoyi" %% "upickle" % "0.6.5"

  /** Tests. */
  val scalatest = "org.scalatest" %% "scalatest" % "3.0.5"

  val akkaVersion = "2.5.11"
}
