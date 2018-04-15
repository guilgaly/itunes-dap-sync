import java.io.File
import java.nio.file.Paths

import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.mp3.MP3File
import org.jaudiotagger.tag.id3.AbstractID3v2Tag
import org.jaudiotagger.tag.vorbiscomment.VorbisCommentTag

val audioFile =
  AudioFileIO
    .read(new File("/Volumes/Data/Music/iTunes/iTunes Media/Music/Bride Adorned/Blessed Stillness/09 Reminiscences & Premonitions.mp3"))
      .asInstanceOf[MP3File]
val tag: AbstractID3v2Tag = audioFile.getID3v2Tag
val artwork = tag.getFirstArtwork


val outputFile =
  AudioFileIO
    .read(new File("/Volumes/SSD_USB/Music/Metal Symphonique/Bride Adorned/Blessed Stillness/09 - Reminiscences & Premonitions.ogg"))
val outputTag = VorbisCommentTag.createNewTag()
outputFile.setTag(outputTag)
outputTag.setField(artwork)

