import mill.scalalib._

implicit class WithOsClassifier(dep: Dep) {
  def withOsClassifier =
    dep.configure(
      coursier.Attributes(
        classifier = coursier.Classifier(WithOsClassifier.osName),
      ),
    )
}

object WithOsClassifier {
  private val osName = System.getProperty("os.name") match {
    case name if name.startsWith("Linux")   => "linux"
    case name if name.startsWith("Mac")     => "mac"
    case name if name.startsWith("Windows") => "win"
    case _                                  => throw new Exception("Unknown platform")
  }
}

/** Scala standard modules (additions to the standard library). */
object scalaModules {
  val parallelCollections = ivy"org.scala-lang.modules::scala-parallel-collections:0.2.0"
}

/** Command-line args parsing. */
val scopt = ivy"com.github.scopt::scopt:3.7.1"

/** GUI framework (JavaFX wrapper for Scala). */
object scalafx {
  private val javaFxVersion = "12.0.2"
  val jfxFxml = ivy"org.openjfx:javafx-fxml:${javaFxVersion}".withOsClassifier
  val jfxMedia = ivy"org.openjfx:javafx-media:${javaFxVersion}".withOsClassifier

  val core = ivy"org.scalafx::scalafx:12.0.2-R18"
  val fxml = ivy"org.scalafx::scalafxml-core-sfx8:0.5"
}

/** Audio files tags.*/
val jaudiotagger = ivy"net.jthink:jaudiotagger:2.2.5"

/** Audio files manipulation (transcoding...) with VLC. */
val vlcj = ivy"uk.co.caprica:vlcj:4.2.0"

/** Audio files manipulation (transcoding...) with ffmpeg. */
val jave = ivy"it.sauronsoftware:jave:1.0.2"

/** Plist parser. */
val ddPlist = ivy"com.googlecode.plist:dd-plist:1.23"

/** Logging. */
object logging {
  private val slf4jVersion = "1.7.29"
  val slf4jApi = ivy"org.slf4j:slf4j-api:${slf4jVersion}"
  val slf4jSimple = ivy"org.slf4j:slf4j-simple:${slf4jVersion}" // logging to System.err for now

  val log4s = ivy"org.log4s::log4s:1.8.2"
}

/** JSON. */
val playJson = ivy"com.typesafe.play::play-json:2.7.4"

/** Enumerations. */
val enumeratum = ivy"com.beachape::enumeratum:1.5.13"

/** Tests. */
val scalatest = ivy"org.scalatest::scalatest:3.0.8"
