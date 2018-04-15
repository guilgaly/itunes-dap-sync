package itsdapsync.tasks

case class TrackSyncResult(persisentId: String,
                           deleteResult: Option[TrackSyncResult.Delete],
                           writeResult: Option[TrackSyncResult.Write])

object TrackSyncResult {

  sealed trait Delete
  case class DeleteSuccess(task: TrackSyncTask.Delete) extends Delete
  case class DeleteFailure(task: TrackSyncTask.Delete, exception: Throwable)
      extends Delete

  sealed trait Write
  case class WriteSuccess(task: TrackSyncTask.Write) extends Write
  case class WriteFailure(task: TrackSyncTask.Write, exception: Throwable)
      extends Write
}
