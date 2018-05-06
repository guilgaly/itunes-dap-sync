package itsdapsync.app.gui

import java.nio.file.Path

import itsdapsync.SyncConfig

/** The configuration used for running the program. */
case class GuiConfig(itunesDirectory: Path, targetDirectory: Path)
    extends SyncConfig.Input
