package itsdapsync.syncdb

import java.time.Instant
import java.util.UUID

import play.api.libs.json.{Json, OFormat}

import scala.collection.immutable.Seq

case class SyncDb(id: UUID, lastSync: LastSync, tracks: Seq[SyncTrack]) {
  lazy val tracksByPersistentID: Map[String, SyncTrack] =
    tracks.map(t => (t.persistentID, t)).toMap
}
object SyncDb {
  val empty = SyncDb(
    UUID.randomUUID(),
    lastSync = LastSync(Instant.EPOCH, Instant.EPOCH),
    Seq.empty
  )

  implicit val format: OFormat[SyncDb] = Json.format
}

case class LastSync(start: Instant, end: Instant)
object LastSync {
  implicit val format: OFormat[LastSync] = Json.format
}

case class SyncTrack(persistentID: String, path: String)
object SyncTrack {
  implicit val format: OFormat[SyncTrack] = Json.format
}
