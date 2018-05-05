package itsdapsync.utils

object FileUtils {

  def normalizeFileName(fileName: String): String = {
    val invalidCharsRegex = """[/<>:"\|\\\?\*]"""
    fileName.replaceAll(invalidCharsRegex, "_")
  }
}
