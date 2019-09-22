package itsdapsync

import java.io.FileNotFoundException
import java.nio.file.{Files, Path}
import java.time.Instant

import itsdapsync.codec.{AfconvertEncoder, Encoder}
import itsdapsync.ituneslib.{ItunesMusicLibrary, ItunesTrack}
import itsdapsync.syncdb.{LastSync, SyncDb, SyncDbEngine, SyncTrack}
import itsdapsync.tags.{DefaultTagger, Tagger}
import itsdapsync.tasks.{SyncTask, TrackSyncResult, TrackSyncTask}
import org.log4s.getLogger

import scala.collection.parallel.CollectionConverters._
import scala.util.{Failure, Success, Try}

object SyncEngine {
  def apply(configInput: SyncConfig.Input): Try[SyncEngine] =
    SyncConfig(configInput).map { config =>
      new SyncEngine(config, AfconvertEncoder, DefaultTagger)
    }
}

class SyncEngine private (
    config: SyncConfig,
    encoder: Encoder,
    tagger: Tagger,
) {
  private[this] val logger = getLogger

  def sync(): Unit = {

    logger.info(s"running with config: $config")

    val startSync = Instant.now()
    val itunesLibrary =
      ItunesMusicLibrary.parseXml(config.itunesXmlLibrary).get
    val oldSyncDb = SyncDbEngine.readSyncDb(config.targetSyncFile)

    val syncTask = SyncTask.newTask(itunesLibrary, oldSyncDb, config)

    val syncResults = syncTask.tracks.par.map(syncTrack).toList

    val endSync = Instant.now()
    val newSyncTracks = syncResults.flatMap {
      case TrackSyncResult.Failure(_, syncTrack, e) =>
        logger.error(e)("") // TODO
        syncTrack
      case TrackSyncResult.Success(_, syncTrack) =>
        syncTrack
    }

    val newSyncDb =
      SyncDb(oldSyncDb.id, LastSync(startSync, endSync), newSyncTracks)
    SyncDbEngine.writeSyncDb(newSyncDb, config.targetSyncFile)
  }

  private def syncTrack(trackSyncTask: TrackSyncTask): TrackSyncResult =
    trackSyncTask match {
      case task: TrackSyncTask.Create =>
        handleCreateTask(task)
      case task: TrackSyncTask.Keep =>
        handleKeepTask(task)
      case task: TrackSyncTask.Replace =>
        handleReplaceTask(task)
      case task: TrackSyncTask.Delete =>
        handleDeleteTask(task)
    }

  private def handleCreateTask(task: TrackSyncTask.Create) =
    createTrack(task.itunesTrack, task.newSyncTrack, task.tempPath) match {
      case Success(_) =>
        TrackSyncResult.Success(task.persisentId, Some(task.newSyncTrack))
      case Failure(e) =>
        TrackSyncResult.Failure(task.persisentId, None, e)
    }

  private def handleKeepTask(task: TrackSyncTask.Keep) = {
    val path = resolveSyncPath(task.oldSyncTrack)
    if (Files.isRegularFile(path))
      TrackSyncResult.Success(task.persisentId, Some(task.oldSyncTrack))
    else
      TrackSyncResult.Failure(
        task.persisentId,
        Some(task.oldSyncTrack),
        new FileNotFoundException(s" Path ${path.toString} not found"),
      )
  }

  private def handleReplaceTask(task: TrackSyncTask.Replace) = {
    deleteTrack(task.oldSyncTrack) match {
      case Success(_) =>
        handleCreateTask(task.asCreateTask)
      case Failure(e) =>
        TrackSyncResult.Failure(task.persisentId, Some(task.oldSyncTrack), e)
    }
  }

  private def handleDeleteTask(task: TrackSyncTask.Delete) =
    deleteTrack(task.oldSyncTrack) match {
      case Success(_) =>
        TrackSyncResult.Success(task.persisentId, None)
      case Failure(e) =>
        TrackSyncResult.Failure(task.persisentId, Some(task.oldSyncTrack), e)
    }

  private def createTrack(
      itunesTrack: ItunesTrack,
      newSyncTrack: SyncTrack,
      tempPath: Path,
  ) = Try {
    val sourcePath = itunesTrack.location
    val targetPath = resolveSyncPath(newSyncTrack)
    logger.info(s"Writing track $targetPath")

    Files.createDirectories(targetPath.getParent)
    encoder.transcode(sourcePath, tempPath)
    tagger.replaceTags(tempPath, itunesTrack)
    Files.copy(tempPath, targetPath)
    Files.delete(tempPath)
  }

  private def deleteTrack(oldSyncTrack: SyncTrack) = Try {
    val path = resolveSyncPath(oldSyncTrack)
    logger.info(s"Deleting track $path")
    Files.deleteIfExists(path)
  }

  private def resolveSyncPath(syncTrack: SyncTrack) =
    config.targetDirectory.resolve(syncTrack.path)
}
