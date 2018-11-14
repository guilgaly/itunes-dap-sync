package itsdapsync.ituneslib

import java.nio.file.Paths
import java.time.Instant

import itsdapsync.testutils.UnitSuite

import scala.util.Success

class ItunesMusicLibraryTest extends UnitSuite {

  test("1 - Empty iTunes library (new library, created by iTunes 12.5.4.42)") {
    val actualLib = "itslib_1_empty_(newly_created).xml"
    val expectedLib = ItunesMusicLibrary(
      majorVersion = 1,
      minorVersion = 1,
      applicationVersion = "12.5.4.42",
      date = Instant.parse("2016-12-16T20:57:02Z"),
      features = 5,
      showContentRatings = true,
      libraryPersistentId = "AD42EFF3B3C9F82E",
      tracks = List(),
      playlists = List(),
      musicFolder = Paths.get("/Volumes/Data/Music/iTunes/iTunes Media")
    )

    parseAndAssert(expectedLib, actualLib)
  }

  test(
    "2 - Basic iTunes library (with two tracks, created by iTunes 12.5.4.42)") {
    val actualLib = "itslib_2_(basic).xml"
    val expectedLib = ItunesMusicLibrary(
      majorVersion = 1,
      minorVersion = 1,
      applicationVersion = "12.5.4.42",
      date = Instant.parse("2016-12-16T21:13:00Z"),
      features = 5,
      showContentRatings = true,
      libraryPersistentId = "AD42EFF3B3C9F82E",
      tracks = List(
        ItunesTrack(
          trackId = 185,
          size = 8442318,
          totalTime = 291422,
          trackNumber = Some(5),
          trackCount = Some(9),
          discNumber = None,
          discCount = None,
          year = Some(1982),
          dateModified = Instant.parse("2016-07-09T12:56:04Z"),
          dateAdded = Instant.parse("2016-12-16T21:12:23Z"),
          bitRate = 231,
          sampleRate = 44100,
          artworkCount = None,
          persistentID = "44FCF6322BC099E1",
          trackType = "File",
          fileFolderCount = 5,
          libraryFolderCount = 1,
          name = Some("The Number of the Beast"),
          artist = Some("Iron Maiden"),
          album = Some("The Number of the Beast"),
          genre = Some("Heavy Metal"),
          compilation = false,
          kind = "Fichier audio MPEG",
          location = Paths.get(
            "/Volumes/Data/Music/iTunes/iTunes Media/Music/Iron Maiden/The Number of the Beast/05 The Number of the Beast.mp3")
        ),
        ItunesTrack(
          trackId = 198,
          size = 7573084,
          totalTime = 293590,
          trackNumber = Some(2),
          trackCount = Some(12),
          discNumber = None,
          discCount = None,
          year = Some(2009),
          dateModified = Instant.parse("2016-07-09T13:00:54Z"),
          dateAdded = Instant.parse("2016-12-16T21:12:54Z"),
          bitRate = 206,
          sampleRate = 44100,
          artworkCount = None,
          persistentID = "EFE20EFB9BC55F02",
          trackType = "File",
          fileFolderCount = 5,
          libraryFolderCount = 1,
          name = Some("Shiva Only Is God"),
          artist = Some("Punish Yourself"),
          album = Some("Pink Panther Party"),
          genre = Some("Metal Indus/Électro"),
          compilation = false,
          kind = "Fichier audio MPEG",
          location = Paths.get(
            "/Volumes/Data/Music/iTunes/iTunes Media/Music/Punish Yourself/Pink Panther Party/02 Shiva Only Is God.mp3")
        )
      ),
      playlists = List(),
      musicFolder = Paths.get("/Volumes/Data/Music/iTunes/iTunes Media")
    )
    parseAndAssert(expectedLib, actualLib)
  }

  // TODO test track with minimal metadata (no ID3 tags)

  private def parseAndAssert(expectedLib: ItunesMusicLibrary,
                             xmlResourcePath: String) = {
    val file = Paths.get(getClass.getResource(xmlResourcePath).toURI)
    ItunesMusicLibrary.parseXml(file) shouldBe Success(expectedLib)
  }
}
