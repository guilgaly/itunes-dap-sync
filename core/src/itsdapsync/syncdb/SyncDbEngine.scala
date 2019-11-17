package itsdapsync.syncdb

import java.nio.file.{Files, Path}

import play.api.libs.json.Json

object SyncDbEngine {
  def readSyncDb(path: Path): SyncDb =
    if (Files.exists(path)) {
      val bytes = Files.readAllBytes(path)
      Json.parse(bytes).as[SyncDb]
    } else {
      SyncDb.empty
    }

  def writeSyncDb(syncDb: SyncDb, path: Path): Unit = {
    val json = Json.toBytes(Json.toJsObject(syncDb))
    Files.write(path, json)
    ()
  }
}
