package itsdapsync.ituneslib

import java.nio.file.Path
import java.time.Instant

import com.dd.plist._
import itsdapsync.ituneslib.NSObjectConverters._

import scala.collection.immutable
import scala.util.Try

object ItunesMusicLibrary {

  /**
   * Parses the "iTunes Library.xml" file to an instance of
   * [[ItunesMusicLibrary]].
   *
   * By default, Itunes only saves its library to a binary file, "iTunes
   * Library.itl". To get the xml version, go to the iTunes Preferences dialog,
   * "advanced" section, and check the box to export library as XML.
   *
   * @param xmlFile The path to the "iTunes Library.xml" file (NOT the "iTunes
   *                Library.itl" file)
   * @return The parsed iTunes library
   */
  def parseXml(xmlFile: Path): Try[ItunesMusicLibrary] = {

    Try {
      val root = PropertyListParser.parse(xmlFile.toFile).toScalaMap

      val parsedTracks = parseTracks(root("Tracks").toScalaMap)
      val parsedPlaylists = parsePlaylists(root("Playlists").toScalaSeq)

      ItunesMusicLibrary(
        majorVersion = root("Major Version").toScalaInt,
        minorVersion = root("Minor Version").toScalaInt,
        applicationVersion = root("Application Version").toScalaString,
        date = root("Date").toJavaInstant,
        features = root("Features").toScalaInt,
        showContentRatings = root("Show Content Ratings").toScalaBoolean,
        libraryPersistentId = root("Library Persistent ID").toScalaString,
        tracks = parsedTracks,
        playlists = parsedPlaylists,
        musicFolder = root("Music Folder").toJavaPath
      )
    }
  }

  private def parseTracks(
      tracks: Map[String, NSObject]): immutable.Seq[ItunesTrack] = {
    val tracksAsMaps = tracks.values.map(_.toScalaMap)
    val filteredTracks =
      tracksAsMaps.filter(_("Track Type").toScalaString == "File")
    filteredTracks.map(parseTrack).toVector.sortBy(_.trackId)
  }

  private def parseTrack(track: Map[String, NSObject]): ItunesTrack =
    ItunesTrack(
      trackId = track("Track ID").toScalaInt,
      size = track("Size").toScalaInt,
      totalTime = track("Total Time").toScalaInt,
      trackNumber = track.get("Track Number").map(_.toScalaInt),
      trackCount = track.get("Track Count").map(_.toScalaInt),
      discNumber = track.get("Disc Number").map(_.toScalaInt),
      discCount = track.get("Disc Count").map(_.toScalaInt),
      year = track.get("Year").map(_.toScalaInt),
      dateModified = track("Date Modified").toJavaInstant,
      dateAdded = track("Date Added").toJavaInstant,
      bitRate = track("Bit Rate").toScalaInt,
      sampleRate = track("Sample Rate").toScalaInt,
      artworkCount = track.get("Artwork Count").map(_.toScalaInt),
      persistentID = track("Persistent ID").toScalaString,
      trackType = track("Track Type").toScalaString,
      fileFolderCount = track("File Folder Count").toScalaInt,
      libraryFolderCount = track("Library Folder Count").toScalaInt,
      name = track.get("Name").map(_.toScalaString),
      artist = track.get("Artist").map(_.toScalaString),
      album = track.get("Album").map(_.toScalaString),
      genre = track.get("Genre").map(_.toScalaString),
      compilation = track.get("Compilation").exists(_.toScalaBoolean),
      kind = track("Kind").toScalaString,
      location = track("Location").toJavaPath
    )

  private def parsePlaylists(
      playlists: Seq[NSObject]): immutable.Seq[ItunesPlaylist] = Vector()
}

// 'Major Version' = '1' (class com.dd.plist.NSNumber)
// 'Minor Version' = '1' (class com.dd.plist.NSNumber)
// 'Application Version' = '12.5.3.17' (class com.dd.plist.NSString)
// 'Date' = 'Tue Dec 13 22:52:58 CET 2016' (class com.dd.plist.NSDate)
// 'Features' = '5' (class com.dd.plist.NSNumber)
// 'Show Content Ratings' = 'true' (class com.dd.plist.NSNumber)
// 'Library Persistent ID' = '2324C8A46BA1C0E0' (class com.dd.plist.NSString)
// 'Tracks' = 'com.dd.plist.NSDictionary@482b2445' (class com.dd.plist.NSDictionary)
// 'Playlists' = 'com.dd.plist.NSArray@680794ba' (class com.dd.plist.NSArray)
// 'Music Folder' = 'file:///Volumes/Data/Music/iTunes/iTunes%20Media/' (class com.dd.plist.NSString)

case class ItunesMusicLibrary(
    majorVersion: Int,
    minorVersion: Int,
    applicationVersion: String,
    date: Instant,
    features: Int,
    showContentRatings: Boolean,
    libraryPersistentId: String,
    tracks: immutable.Seq[ItunesTrack],
    playlists: immutable.Seq[ItunesPlaylist],
    musicFolder: Path
)

// 'Track ID' = '1731' (class com.dd.plist.NSNumber)
// 'Size' = '8093192' (class com.dd.plist.NSNumber)
// 'Total Time' = '265848' (class com.dd.plist.NSNumber)
// 'Track Number' = '1' (class com.dd.plist.NSNumber)
// 'Track Count' = '5' (class com.dd.plist.NSNumber)
// 'Year' = '2007' (class com.dd.plist.NSNumber)
// 'Date Modified' = 'Sat Jul 09 14:47:12 CEST 2016' (class com.dd.plist.NSDate)
// 'Date Added' = 'Sun Nov 13 19:32:54 CET 2016' (class com.dd.plist.NSDate)
// 'Bit Rate' = '241' (class com.dd.plist.NSNumber)
// 'Sample Rate' = '44100' (class com.dd.plist.NSNumber)
// 'Artwork Count' = '1' (class com.dd.plist.NSNumber)
// 'Persistent ID' = '70DAC253B2A40BD1' (class com.dd.plist.NSString)
// 'Track Type' = 'File' (class com.dd.plist.NSString)
// 'File Folder Count' = '5' (class com.dd.plist.NSNumber)
// 'Library Folder Count' = '1' (class com.dd.plist.NSNumber)
// 'Name' = 'Matraquage' (class com.dd.plist.NSString)
// 'Artist' = 'Abhora' (class com.dd.plist.NSString)
// 'Album' = 'Noircir Sa Page' (class com.dd.plist.NSString)
// 'Genre' = 'Metal Divers' (class com.dd.plist.NSString)
// 'Kind' = 'Fichier audio MPEG' (class com.dd.plist.NSString)
// 'Location' = 'file:///Volumes/Data/Music/iTunes/iTunes%20Media/Music/Abhora/Noircir%20Sa%20Page/01%20Matraquage.mp3' (class com.dd.plist.NSString)

case class ItunesTrack(
    trackId: Int,
    size: Int,
    totalTime: Int,
    trackNumber: Option[Int],
    trackCount: Option[Int],
    discNumber: Option[Int],
    discCount: Option[Int],
    year: Option[Int],
    dateModified: Instant,
    dateAdded: Instant,
    bitRate: Int,
    sampleRate: Int,
    artworkCount: Option[Int],
    persistentID: String,
    trackType: String,
    fileFolderCount: Int,
    libraryFolderCount: Int,
    name: Option[String],
    artist: Option[String],
    album: Option[String],
    genre: Option[String],
    compilation: Boolean,
    kind: String,
    location: Path
)

// TODO parse playlists in the iTunes library
case class ItunesPlaylist()
