package itsdapsync.app.gui

import java.nio.file.{Path, Paths}
import java.util.prefs.Preferences

object AppPreferences {
  private lazy val prefs = Preferences.userRoot.node("itsdapsync.app.gui")

  private val lastSourcePathKey = "lastSourcePath"

  def lastSourcePath_=(path: Option[Path]): Unit =
    putPathOption(lastSourcePathKey, path)

  def lastSourcePath: Option[Path] =
    getPathOption(lastSourcePathKey)

  private val lastTargetPathKey = "lastTargetPath"

  def lastTargetPath_=(path: Option[Path]): Unit =
    putPathOption(lastTargetPathKey, path)

  def lastTargetPath: Option[Path] =
    getPathOption(lastTargetPathKey)

  private def putPathOption(key: String, path: Option[Path]): Unit = {
    val value = path.map(_.toString).getOrElse("")
    prefs.put(key, value)
  }

  private def getPathOption(key: String) =
    prefs.get(key, "") match {
      case ""  => None
      case str => Some(Paths.get(str))
    }
}
