package itsdapsync

import java.nio.file.{Files, Path}

import scala.util.Try

case class CleanConfig(targetDirectory: Path, targetSyncFile: Path)

object CleanConfig {
  def apply(config: Input): Try[CleanConfig] = Try {
    val targetSyncFile: Path = config.targetDirectory.resolve("itsdapsync.json")
    require(Files.isRegularFile(targetSyncFile)) // TODO

    CleanConfig(config.targetDirectory, targetSyncFile)
  }

  trait Input {
    def targetDirectory: Path
  }
}
