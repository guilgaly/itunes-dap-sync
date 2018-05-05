package itsdapsync.codec

import java.nio.file.Path

trait Encoder extends AutoCloseable {
  def transcode(sourceFile: Path, targetFile: Path)
}
