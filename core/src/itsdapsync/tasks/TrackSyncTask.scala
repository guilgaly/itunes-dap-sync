package itsdapsync.tasks

import java.nio.file.Path

import itsdapsync.syncdb.SyncTrack
import itsdapsync.ituneslib.ItunesTrack

sealed trait TrackSyncTask {
  def persisentId: String
}

object TrackSyncTask {
  case class Create(
      itunesTrack: ItunesTrack,
      newSyncTrack: SyncTrack,
      tempPath: Path,
  ) extends TrackSyncTask {
    require(itunesTrack.persistentID == newSyncTrack.persistentID)

    override def persisentId: String = itunesTrack.persistentID
  }

  case class Keep(oldSyncTrack: SyncTrack) extends TrackSyncTask {
    override def persisentId: String = oldSyncTrack.persistentID
  }

  case class Replace(
      oldSyncTrack: SyncTrack,
      itunesTrack: ItunesTrack,
      newSyncTrack: SyncTrack,
      tempPath: Path,
  ) extends TrackSyncTask {
    require(itunesTrack.persistentID == oldSyncTrack.persistentID)
    require(itunesTrack.persistentID == newSyncTrack.persistentID)

    override def persisentId: String = itunesTrack.persistentID

    def asCreateTask: Create = Create(itunesTrack, newSyncTrack, tempPath)
  }

  case class Delete(oldSyncTrack: SyncTrack) extends TrackSyncTask {
    override def persisentId: String = oldSyncTrack.persistentID
  }
}
