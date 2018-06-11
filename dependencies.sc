import mill.scalalib._

object compilerPlugins {
  val macroParadise = ivy"org.scalamacros:::paradise:2.1.0"
}

/** Command-line args parsing. */
val scopt = ivy"com.github.scopt::scopt:3.7.0"

/** GUI framework (JavaFX wrapper for Scala). */
object scalafx {
  val core = ivy"org.scalafx::scalafx:8.0.144-R12"
  val fxml = ivy"org.scalafx::scalafxml-core-sfx8:0.4"
}

/** Audio files tags.*/
val jaudiotagger = ivy"net.jthink:jaudiotagger:2.2.5"

/** Audio files manipulation (transcoding...) with VLC. */
val vlcj = ivy"uk.co.caprica:vlcj:3.10.1"

/** Audio files manipulation (transcoding...) with ffmpeg. */
val jave = ivy"it.sauronsoftware:jave:1.0.2"

/** Plist parser. */
val ddPlist = ivy"com.googlecode.plist:dd-plist:1.20"

/** Logging. */
object logging {
  val log4s = ivy"org.log4s::log4s:1.6.1"
  val slf4jApi = ivy"org.slf4j:slf4j-api:1.7.25"
  // logging to System.err for now
  val slf4jSimple = ivy"org.slf4j:slf4j-simple:1.7.25"
}

/** JSON. */
val playJson = ivy"com.typesafe.play::play-json:2.6.9"

/** Enumerations. */
val enumeratum = ivy"com.beachape::enumeratum:1.5.13"

/** Tests. */
val scalatest = ivy"org.scalatest::scalatest:3.0.5"