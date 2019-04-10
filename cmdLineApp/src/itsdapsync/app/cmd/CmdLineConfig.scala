package itsdapsync.app.cmd

import java.io.File
import java.nio.file.{Path, Paths}

import itsdapsync.SyncConfig
import scopt.OptionParser

object CmdLineConfig {
  private val parser = new OptionParser[CmdLineConfig]("itunes-dap-sync") {
    head("itunes-dap-sync")

    help("help").text("prints this usage text")

    arg[File]("<iTunes directory>")
      .text(
        "iTunes' home directory, where iTunes stores media files and metadata (typically '~/Music/iTunes' or '%userprofile%\\Music\\iTunes')",
      )
      .action(
        (file: File, config: CmdLineConfig) =>
          config.copy(itunesDirectory = file.toPath),
      )

    arg[File]("<target directory>")
      .text("The target directory, where music files will be copied")
      .action(
        (file: File, config: CmdLineConfig) =>
          config.copy(targetDirectory = file.toPath),
      )

    // Override terminate to exit normaly (returning None) when help or version
    // is invoked
    override def terminate(exitState: Either[String, Unit]): Unit = ()
  }

  /**
   * Parses the command line arguments.
   *
   * @param args The command line arguments
   * @return The configuration parsed from the given `args`
   */
  def parse(args: Seq[String]): Option[CmdLineConfig] =
    parser.parse(args, new CmdLineConfig())
}

/** The configuration used for running the program. */
case class CmdLineConfig(itunesDirectory: Path, targetDirectory: Path)
    extends SyncConfig.Input {

  /** No-args constructor used only for the scopt parser. */
  private def this() = this(Paths.get("."), Paths.get("."))
}
