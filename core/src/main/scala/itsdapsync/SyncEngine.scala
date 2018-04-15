package itsdapsync

import java.nio.file.Files
import java.time.Instant

import itsdapsync.codec.{Encoder, XldEncoder}
import itsdapsync.ituneslib.ItunesMusicLibrary
import itsdapsync.syncdb.{LastSync, SyncDb, SyncDbEngine}
import itsdapsync.tags.{DefaultTagger, Tagger}
import itsdapsync.tasks.{SyncTask, TrackSyncResult, TrackSyncTask}
import org.log4s.getLogger

import scala.util.Try
import scala.util.control.NonFatal

object SyncEngine {
  def apply(configInput: ConfigInput): Try[SyncEngine] =
    Config(configInput).map { config =>
      new SyncEngine(config, XldEncoder, DefaultTagger)
    }
}

class SyncEngine private (config: Config, encoder: Encoder, tagger: Tagger) {
  private[this] val logger = getLogger

  def sync(): Unit = {

    logger.info(s"running with config: $config")
    val syncDbPath = config.targetDirectory.resolve("iTunesDapSync.json")

    val startSync = Instant.now()
    val itunesLibrary =
      ItunesMusicLibrary.parseXml(config.itunesXmlLibrary).get
    val oldSyncDb = SyncDbEngine.readSyncDb(syncDbPath)

    val syncTask = SyncTask.newTask(itunesLibrary, oldSyncDb, config)

    val syncResults = syncTask.tracks.map(syncTrack).toList
    val endSync = Instant.now()
    val newSyncTracks = {
      syncResults.flatMap { res =>
        val newSyncTrack = res.writeResult.flatMap {
          case TrackSyncResult.WriteSuccess(trk) =>
            Some(trk.newSyncTrack)
          case TrackSyncResult.WriteFailure(trk, e) =>
            logger.error(e)("") // TODO
            None
        }
        val oldSyncTrack = res.deleteResult.flatMap {
          case TrackSyncResult.DeleteSuccess(_) =>
            None
          case TrackSyncResult.DeleteFailure(trk, _)
              if newSyncTrack.exists(_.path == trk.oldSyncTrack.path) =>
            None
          case TrackSyncResult.DeleteFailure(trk, e) =>
            logger.error(e)("") // TODO
            Some(trk.oldSyncTrack)
        }

        Seq(newSyncTrack, oldSyncTrack).flatten
      }
    }

    val newSyncDb =
      SyncDb(oldSyncDb.id, LastSync(startSync, endSync), newSyncTracks)
    SyncDbEngine.writeSyncDb(newSyncDb, syncDbPath)
  }

  private def syncTrack(trackSyncTask: TrackSyncTask): TrackSyncResult = {
    val deleteResult = trackSyncTask.deleteTask.map(executeDeleteTask)
    val writeResult = trackSyncTask.writeTask.map(executeWriteTask)
    TrackSyncResult(trackSyncTask.persisentId, deleteResult, writeResult)
  }

  private def executeDeleteTask(
      deleteTask: TrackSyncTask.Delete): TrackSyncResult.Delete = {
    try {
      val path = config.targetDirectory.resolve(deleteTask.oldSyncTrack.path)
      logger.info(s"Deleting track $path")
      Files.deleteIfExists(path)
      TrackSyncResult.DeleteSuccess(deleteTask)
    } catch {
      case NonFatal(e) => TrackSyncResult.DeleteFailure(deleteTask, e)
    }
  }

  private def executeWriteTask(
      writeTask: TrackSyncTask.Write): TrackSyncResult.Write = {
    try {
      val sourcePath = writeTask.itunesTrack.location
      val tempPath = writeTask.tempPath
      val targetPath =
        config.targetDirectory.resolve(writeTask.newSyncTrack.path)

      logger.info(s"Writing track $targetPath")

      Files.createDirectories(targetPath.getParent)
      encoder.transcode(sourcePath, tempPath)
      tagger.replaceTags(tempPath, writeTask.itunesTrack)
      Files.copy(tempPath, targetPath)
      Files.delete(tempPath)
      TrackSyncResult.WriteSuccess(writeTask)
    } catch {
      case NonFatal(e) => TrackSyncResult.WriteFailure(writeTask, e)
    }
  }
}
