package itsdapsync.tasks

import itsdapsync.syncdb.SyncTrack

sealed trait TrackSyncResult {
  def persisentId: String
  def syncTrack: Option[SyncTrack]
}

object TrackSyncResult {

  case class Success(persisentId: String, syncTrack: Option[SyncTrack])
      extends TrackSyncResult

  case class Failure(
      persisentId: String,
      syncTrack: Option[SyncTrack],
      error: Throwable,
  ) extends TrackSyncResult
}
