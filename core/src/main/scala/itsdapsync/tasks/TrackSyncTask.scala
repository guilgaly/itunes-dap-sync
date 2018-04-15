package itsdapsync.tasks

import java.nio.file.Path

import itsdapsync.syncdb.SyncTrack
import itsdapsync.ituneslib.ItunesTrack

case class TrackSyncTask(persisentId: String,
                         deleteTask: Option[TrackSyncTask.Delete],
                         writeTask: Option[TrackSyncTask.Write])

object TrackSyncTask {

  def delete(oldSyncTrack: SyncTrack): TrackSyncTask =
    TrackSyncTask(oldSyncTrack.persistentID, Some(Delete(oldSyncTrack)), None)

  def create(itunesTrack: ItunesTrack,
             newSyncTrack: SyncTrack,
             tempPath: Path): TrackSyncTask = {
    TrackSyncTask(itunesTrack.persistentID,
                  None,
                  Some(Write(itunesTrack, newSyncTrack, tempPath)))
  }

  def replace(oldSyncTrack: SyncTrack,
              itunesTrack: ItunesTrack,
              newSyncTrack: SyncTrack,
              tempPath: Path): TrackSyncTask = {
    require(itunesTrack.persistentID == oldSyncTrack.persistentID)
    require(itunesTrack.persistentID == newSyncTrack.persistentID)
    TrackSyncTask(itunesTrack.persistentID,
                  Some(Delete(oldSyncTrack)),
                  Some(Write(itunesTrack, newSyncTrack, tempPath)))
  }

  def doNothing(persisentId: String): TrackSyncTask =
    TrackSyncTask(persisentId, None, None)

  case class Delete(oldSyncTrack: SyncTrack)

  case class Write(itunesTrack: ItunesTrack,
                   newSyncTrack: SyncTrack,
                   tempPath: Path)
}
