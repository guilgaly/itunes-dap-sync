package itsdapsync

import java.time.Instant

import itsdapsync.ituneslib.ItunesMusicLibrary
import itsdapsync.syncdb.SyncDbEngine
import org.log4s.getLogger

import scala.util.Try

object CleanEngine {
  def apply(configInput: CleanConfig.Input): Try[CleanEngine] =
    CleanConfig(configInput).map { config =>
      new CleanEngine(config)
    }
}

class CleanEngine(config: CleanConfig) {
  private[this] val logger = getLogger

  def clean(): Unit = {
    val startClean = Instant.now()
    val oldSyncDb = SyncDbEngine.readSyncDb(config.targetSyncFile)

  }
}
