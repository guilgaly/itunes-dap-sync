package itsdapsync.codec
import java.nio.file.Path

import scala.sys.process.Process

object XldEncoder extends Encoder {

  override def transcode(sourceFile: Path, targetFile: Path): Unit = {
    val profile = "AAC 160"
    val inputPath = sourceFile.toAbsolutePath.toString
    val outputPath = targetFile.toAbsolutePath.toString
    val cmd = Seq("xld", "--profile", profile, "-o", outputPath, inputPath)

    Process(cmd).!!
  }

  override def close(): Unit = ()
}
