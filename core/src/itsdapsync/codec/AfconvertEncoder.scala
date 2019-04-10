package itsdapsync.codec
import java.nio.file.Path

import org.log4s.getLogger

import scala.sys.process.Process

object AfconvertEncoder extends Encoder {
  private[this] val logger = getLogger

  override def transcode(sourceFile: Path, targetFile: Path): Unit = {
    val fileFormat = "m4af"
    val dataFormat = "aac"
    val bitrate = "128000"
    val codecQuality = "127"
    val codecStrategy = "2" // 0 for CBR, 1 for ABR, 2 for VBR_constrained, 3 for VBR

    val inputPath = sourceFile.toAbsolutePath.toString
    val outputPath = targetFile.toAbsolutePath.toString

    val cmd = Seq(
      "afconvert",
      "--file",
      fileFormat,
      "--data",
      dataFormat,
      "--bitrate",
      bitrate,
      "--quality",
      codecQuality,
      "--strategy",
      codecStrategy,
      inputPath,
      outputPath,
    )

    logger.debug(s"Executing: '${cmd.mkString(" ")}'")

    Process(cmd).!!
    ()
  }

  override def close(): Unit = ()
}
