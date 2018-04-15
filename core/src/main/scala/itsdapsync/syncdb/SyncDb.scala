package itsdapsync.syncdb

import java.time.Instant
import java.util.UUID

import itsdapsync.json.CustomPickler._

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

  implicit val rw: RW[SyncDb] = macroRW[SyncDb]
}

case class LastSync(start: Instant, end: Instant)
object LastSync {
  implicit val rw: RW[LastSync] = macroRW[LastSync]
}

case class SyncTrack(persistentID: String, path: String)
object SyncTrack {
  implicit val rw: RW[SyncTrack] = macroRW[SyncTrack]
}
