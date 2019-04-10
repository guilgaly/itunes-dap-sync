package itsdapsync.tasks

import itsdapsync.SyncConfig
import itsdapsync.ituneslib.{ItunesMusicLibrary, ItunesTrack}
import itsdapsync.syncdb.{SyncDb, SyncTrack}
import itsdapsync.utils.FileUtils.normalizeFileName

import scala.collection.immutable.Seq

case class SyncTask(tracks: Seq[TrackSyncTask])

object SyncTask {

  def newTask(
      itunesLibrary: ItunesMusicLibrary,
      oldSyncDb: SyncDb,
      config: SyncConfig,
  ): SyncTask = {

    def resolveTargetPath(track: ItunesTrack) = {
      def genreDir = track.genre.getOrElse("Unknown Genre")
      def artistDir = {
        if (track.compilation)
          "Compilation"
        else
          track.artist.getOrElse("Unknown Artist")
      }
      def albumDir = track.album.getOrElse("Unknown Album")
      def fileName = {
        val discNoPart = track.discNumber.map(n => f"$n%02d - ").getOrElse("")
        val trackNoPart = track.trackNumber.map(n => f"$n%02d - ").getOrElse("")
        val trackNamePart = track.name.getOrElse(track.persistentID)
        s"$discNoPart$trackNoPart$trackNamePart.m4a"
      }

      val targetPath =
        config.targetDirectory
          .resolve(normalizeFileName(genreDir))
          .resolve(normalizeFileName(artistDir))
          .resolve(normalizeFileName(albumDir))
          .resolve(normalizeFileName(fileName))

      config.targetDirectory.relativize(targetPath).toString
    }

    def resolveTempPath(track: ItunesTrack) =
      config.tempDirectory.resolve(
        normalizeFileName(s"${track.persistentID}.m4a"),
      )

    val deleteOnlytasks = {
      val itunesIds = itunesLibrary.tracks.map(_.persistentID).toSet
      oldSyncDb.tracks.collect {
        case syncTrack if !itunesIds(syncTrack.persistentID) =>
          TrackSyncTask.Delete(syncTrack)
      }
    }

    val createOrReplaceTasks = {
      itunesLibrary.tracks.map { track =>
        val targetPath = resolveTargetPath(track)
        val tempPath = resolveTempPath(track)
        val newSyncTrack = SyncTrack(track.persistentID, targetPath)
        oldSyncDb.tracksByPersistentID.get(track.persistentID) match {
          case Some(oldSyncTrack) =>
            if (track.dateModified.isAfter(oldSyncDb.lastSync.start))
              TrackSyncTask.Replace(oldSyncTrack, track, newSyncTrack, tempPath)
            else
              TrackSyncTask.Keep(oldSyncTrack)
          case None =>
            TrackSyncTask.Create(track, newSyncTrack, tempPath)
        }
      }
    }

    SyncTask(deleteOnlytasks ++ createOrReplaceTasks)
  }
}
