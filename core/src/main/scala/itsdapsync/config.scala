package itsdapsync

import java.nio.file.{Files, Path}

import scala.util.Try

trait ConfigInput {
  def itunesDirectory: Path
  def targetDirectory: Path
}

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
case class Config(itunesDirectory: Path,
                  targetDirectory: Path,
                  tempDirectory: Path,
                  itunesBinaryLibrary: Path,
                  itunesXmlLibrary: Path,
                  targetSyncFile: Path) {

  override def toString: String =
    s"${getClass.getSimpleName}($itunesDirectory, $targetDirectory, $tempDirectory)"
}

object Config {
  def apply(config: ConfigInput): Try[Config] = Try {
    val tempDirectory: Path =
      Files.createTempDirectory("itunes-dap-sync")
    val itunesBinaryLibrary: Path =
      config.itunesDirectory.resolve("iTunes Library.itl")
    require(Files.isRegularFile(itunesBinaryLibrary)) // TODO
    val itunesXmlLibrary: Path =
      config.itunesDirectory.resolve("iTunes Music Library.xml")
    require(Files.isRegularFile(itunesXmlLibrary)) // TODO
    val targetSyncFile: Path =
      config.targetDirectory.resolve("itsdapsync.json")
    // TODO checks for targetSyncFile

    Config(config.itunesDirectory,
           config.targetDirectory,
           tempDirectory,
           itunesBinaryLibrary,
           itunesXmlLibrary,
           targetSyncFile)
  }
}
