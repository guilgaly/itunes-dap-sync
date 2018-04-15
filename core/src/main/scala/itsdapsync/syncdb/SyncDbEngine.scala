package itsdapsync.syncdb

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}

import itsdapsync.json.CustomPickler

object SyncDbEngine {

  def readSyncDb(path: Path): SyncDb =
    if (Files.exists(path)) {
      val bytes = Files.readAllBytes(path)
      CustomPickler.read[SyncDb](bytes)
    } else {
      SyncDb.empty
    }

  def writeSyncDb(syncDb: SyncDb, path: Path): Unit = {
    val json = CustomPickler.write(syncDb).getBytes(StandardCharsets.UTF_8)
    Files.write(path, json)
  }
}
