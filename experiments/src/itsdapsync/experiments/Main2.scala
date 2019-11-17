package itsdapsync.experiments

import java.io.File
import java.nio.file.Files

import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.vorbiscomment.VorbisCommentTag
object Main2 extends App {
  val root = new File("/Volumes/SSD_USB/Music")
  recurs(root)

  def recurs(folder: File): Unit = {
    val oggFiles = folder.listFiles((dir, name) => name.endsWith(".ogg"))
    if (oggFiles.nonEmpty) {
      val oggFile = oggFiles.head
      val audioFile = AudioFileIO.read(oggFile)
      val tag = audioFile.getTag.asInstanceOf[VorbisCommentTag]
      val artwork = tag.getFirstArtwork
      if (artwork != null) {
        val mimeType = artwork.getMimeType
        val imageFileName =
          if ("image/jpeg" == mimeType)
            "cover.jpg"
          else if ("image/png" == mimeType)
            "cover.png"
          else if ("image/gif" == mimeType)
            "cover.gif"
          else
            null
        if (imageFileName != null) {
          val imageFile = new File(folder, imageFileName)
          Files.write(imageFile.toPath, artwork.getBinaryData)
        }
      }
    } else {
      val children = folder.listFiles(_.isDirectory)
      for (child <- children) {
        recurs(child)
      }
    }
  }
}
