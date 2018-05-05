package itsdapsync

import java.nio.file.{Files, Path}

import scala.util.Try

/**
 * @param itunesDirectory iTunes' home directory, where iTunes stores media
 *                        files and metadata
 * @param targetDirectory The target directory to which the iTunes library
 *                        content should by sync'ed
 * @param tempDirectory A place to write temporary files
 * @param itunesBinaryLibrary The path to iTunes' binary library file ("iTunes
 *                            Library.itl")
 * @param itunesXmlLibrary The path to iTunes' XML library file ("iTunes Music
 *                         Library.xml")
 * @param targetSyncFile The path to the file listing already sync'ed files on
 *                       the target.
 */
case class SyncConfig(itunesDirectory: Path,
                      targetDirectory: Path,
                      tempDirectory: Path,
                      itunesBinaryLibrary: Path,
                      itunesXmlLibrary: Path,
                      targetSyncFile: Path) {

  override def toString: String =
    s"${getClass.getSimpleName}($itunesDirectory, $targetDirectory, $tempDirectory)"
}

object SyncConfig {
  def apply(config: Input): Try[SyncConfig] = Try {
    val tempDirectory: Path =
      Files.createTempDirectory("itunes-dap-sync")
    val itunesBinaryLibrary: Path =
      config.itunesDirectory.resolve("iTunes Library.itl")
    require(Files.isRegularFile(itunesBinaryLibrary)) // TODO
    val itunesXmlLibrary: Path =
      config.itunesDirectory.resolve("iTunes Music Library.xml")
    require(Files.isRegularFile(itunesXmlLibrary)) // TODO
    val targetSyncFile: Path = config.targetDirectory.resolve("itsdapsync.json")
    // TODO checks for targetSyncFile

    SyncConfig(config.itunesDirectory,
               config.targetDirectory,
               tempDirectory,
               itunesBinaryLibrary,
               itunesXmlLibrary,
               targetSyncFile)
  }

  trait Input {
    def itunesDirectory: Path
    def targetDirectory: Path
  }
}
