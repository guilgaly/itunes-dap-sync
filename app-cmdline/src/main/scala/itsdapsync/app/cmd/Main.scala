package itsdapsync.app.cmd

import itsdapsync.SyncEngine
import org.log4s._

object Main extends App {

  private[this] val logger = getLogger

  CmdLineConfig.parse(args) match {
    case Some(config) =>
      executeSync(config)
    case None =>
    // Do nothing and exit
  }

  private def executeSync(config: CmdLineConfig): Unit = {
    logger.info(s"Executing sync with parsed conf: $config")
    SyncEngine(config).map(_.sync()).get
  }
}
