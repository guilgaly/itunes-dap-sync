package itsdapsync.tags

import java.nio.file.Path

import itsdapsync.ituneslib.ItunesTrack
import org.jaudiotagger.audio.mp3.MP3File
import org.jaudiotagger.audio.{AudioFile, AudioFileIO}
import org.jaudiotagger.tag.images.Artwork
import org.jaudiotagger.tag.mp4.Mp4Tag
import org.jaudiotagger.tag.reference.ID3V2Version
import org.jaudiotagger.tag.{FieldKey, Tag, TagOptionSingleton}

object DefaultTagger extends Tagger {
  TagOptionSingleton.getInstance().setPadNumbers(true)
  TagOptionSingleton.getInstance().setID3V2Version(ID3V2Version.ID3_V24)

  override def replaceTags(targetFile: Path, itunesTrack: ItunesTrack): Unit = {
    val audioFile = AudioFileIO.read(targetFile.toFile)

    val (tag: Tag, artwork: Option[Artwork]) = audioFile match {
      case mp3File: MP3File => extractArtworkAndResetTagMp3(mp3File)
      case _                => extractArtworkAndResetTag(audioFile)
    }

    updateTag(tag, artwork, itunesTrack)

    audioFile.commit()
  }

  private def extractArtworkAndResetTagMp3(mp3File: MP3File) = {
    val (id3v1Tag, id3v2Tag) = (mp3File.getID3v1Tag, mp3File.getID3v2Tag)
    val artwork =
      if (id3v2Tag != null && id3v2Tag.getFirstArtwork != null)
        id3v2Tag.getFirstArtwork
      else if (id3v1Tag != null)
        id3v1Tag.getFirstArtwork
      else
        null

    mp3File.setID3v1Tag(null)
    mp3File.setID3v2Tag(null)
    val tag = mp3File.getTagAndConvertOrCreateAndSetDefault

    (tag, Option(artwork))
  }

  private def extractArtworkAndResetTag(audioFile: AudioFile) = {
    val artwork =
      if (audioFile.getTag != null)
        audioFile.getTag.getFirstArtwork
      else
        null
    val tag = new Mp4Tag
    audioFile.setTag(tag)
    (tag, Option(artwork))
  }

  private def updateTag(
      tag: Tag,
      artwork: Option[Artwork],
      itunesTrack: ItunesTrack,
  ): Unit = {
    itunesTrack.name.foreach(tag.setField(FieldKey.TITLE, _))
    itunesTrack.trackNumber.foreach { x =>
      tag.setField(FieldKey.TRACK, x.toString)
    }
    itunesTrack.trackCount.foreach { x =>
      tag.setField(FieldKey.TRACK_TOTAL, x.toString)
    }
    itunesTrack.discNumber.foreach { x =>
      tag.setField(FieldKey.DISC_NO, x.toString)
    }
    itunesTrack.discCount.foreach { x =>
      tag.setField(FieldKey.DISC_TOTAL, x.toString)
    }
    itunesTrack.year.foreach { x =>
      tag.setField(FieldKey.YEAR, x.toString)
    }
    itunesTrack.artist.foreach(tag.setField(FieldKey.ARTIST, _))
    itunesTrack.album.foreach(tag.setField(FieldKey.ALBUM, _))
    itunesTrack.genre.foreach(tag.setField(FieldKey.GENRE, _))
    artwork.foreach(tag.setField)
  }
}
