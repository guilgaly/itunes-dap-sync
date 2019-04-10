package itsdapsync.app.gui

import java.io.{PrintWriter, StringWriter}
import java.nio.file.{Files, Path}

import itsdapsync.SyncEngine
import itsdapsync.utils.Platform
import org.log4s.getLogger
import scalafx.Includes._
import scalafx.application.{Platform => JFXPlatform}
import scalafx.beans.binding.Bindings
import scalafx.beans.property.{BooleanProperty, ObjectProperty}
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control._
import scalafx.scene.input.{KeyCode, KeyCodeCombination, KeyCombination}
import scalafx.stage.DirectoryChooser
import scalafxml.core.macros.sfxml

import scala.util.{Failure, Success}

trait MainControllerInterface {
  def onStop(): Unit
}

@sfxml
class MainController(
    menuBar: MenuBar,
    fileMenu: Menu,
    sourcePathTextField: TextField,
    sourcePathChoiceButton: Button,
    targetPathTextField: TextField,
    targetPathChoiceButton: Button,
    startSyncButton: Button,
) extends MainControllerInterface {
  private[this] val logger = getLogger

  adaptMenusToPlatform()

  private val syncing = BooleanProperty(false)
  sourcePathChoiceButton.disable <== syncing
  targetPathChoiceButton.disable <== syncing
  startSyncButton.disable <== syncing

  private val sourcePath =
    bindPath(sourcePathTextField, AppPreferences.lastSourcePath)
  private val targetPath =
    bindPath(targetPathTextField, AppPreferences.lastTargetPath)

  def onChooseSourcePath(): Unit =
    onChoosePath(sourcePath, sourcePathTextField, "iTunes library")

  def onChooseTargetPath(): Unit =
    onChoosePath(targetPath, targetPathTextField, "target folder")

  def onStartSync(): Unit = (sourcePath(), targetPath()) match {
    case (Some(source), Some(target)) =>
      doSync(source, target)
    case _ =>
      showMissingField(sourcePath, sourcePathTextField)
      showMissingField(targetPath, targetPathTextField)
  }

  def onStop(): Unit = {
    AppPreferences.lastSourcePath = sourcePath()
    AppPreferences.lastTargetPath = targetPath()
  }

  private def onChoosePath(
      path: ObjectProperty[Option[Path]],
      pathTextField: TextField,
      name: String,
  ): Unit = {
    val dirChooser = new DirectoryChooser {
      title = s"Pick a folder for the $name"
      initialDirectory = path.value match {
        case Some(p) if Files.isDirectory(p) => p.toFile
        case _                               => initialDirectory.value
      }
    }
    val choosenPath = Option {
      dirChooser.showDialog(getWindow)
    }.map(_.toPath)
    path.set(choosenPath)
    showMissingField(path, pathTextField)
  }

  private def doSync(source: Path, target: Path): Unit = {
    syncing.set(true) // Not very useful right now because the sync process is still synchronous...

    val config = GuiConfig(source, target)

    logger.info(s"Executing sync with conf: $config")
    SyncEngine(config).map(_.sync()) match {
      case Success(_) =>
        new Alert(AlertType.Information) {
          initOwner(getWindow)
          title = ""
          headerText = "Success!"
          contentText = "Target was successfuly synchronized with the library."
        }.showAndWait()
      case Failure(e) =>
        val errorText = new StringWriter
        e.printStackTrace(new PrintWriter(errorText))
        new Alert(AlertType.Error) {
          initOwner(getWindow)
          title = ""
          headerText = "Error"
          contentText = errorText.toString
        }.showAndWait()
    }

    syncing.set(false)
  }

  private def showMissingField[T](
      prop: ObjectProperty[Option[T]],
      field: TextInputControl,
  ): Unit = {
    if (prop().isEmpty) {
      field.styleClass.add("error")
    } else {
      field.styleClass.remove("error")
    }
    ()
  }

  private def bindPath(pathTextField: TextField, defaultPath: Option[Path]) = {
    val path = ObjectProperty(defaultPath)
    pathTextField.text <== Bindings.createStringBinding(
      () => path.value.map(_.toString).getOrElse(""),
      path,
    )
    path
  }

  private def adaptMenusToPlatform(): Unit = {
    if (Platform.current != Platform.MacPlatform) {
      val quitMenuItem = new MenuItem {
        text = "Quit"
        accelerator = new KeyCodeCombination(
          KeyCode.Q,
          KeyCombination.ShortcutDown,
        )
        onAction = _ => JFXPlatform.exit()
      }

      fileMenu.items += quitMenuItem
    }
    ()
  }

  private def getWindow = menuBar.getScene.getWindow
}
