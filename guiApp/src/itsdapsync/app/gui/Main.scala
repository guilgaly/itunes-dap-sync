package itsdapsync.app.gui

import java.io.{PrintWriter, StringWriter}
import java.nio.file.Path

import itsdapsync.SyncEngine
import itsdapsync.utils.Platform
import org.log4s.getLogger
import scalafx.Includes._
import scalafx.application.JFXApp.PrimaryStage
import scalafx.application.{JFXApp, Platform => JFXPlatform}
import scalafx.beans.binding.Bindings
import scalafx.beans.property.{BooleanProperty, ObjectProperty}
import scalafx.geometry.{HPos, Insets, VPos}
import scalafx.scene.Scene
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Label, _}
import scalafx.scene.input.{KeyCode, KeyCodeCombination, KeyCombination}
import scalafx.scene.layout.Priority._
import scalafx.scene.layout._
import scalafx.stage.DirectoryChooser

import scala.util.{Failure, Success}

object Main extends JFXApp {
  private[this] val logger = getLogger

  private val syncing = BooleanProperty(false)

  private val (sourcePath, sourceLabel, sourceTextField, sourceButton) =
    directorySelectComponents("iTunes library")
  sourcePath.set(AppPreferences.lastSourcePath)

  private val (targetPath, targetLabel, targetTextField, targetButton) =
    directorySelectComponents("target folder")
  targetPath.set(AppPreferences.lastTargetPath)

  private val syncButton = new Button {
    text = "Sync now!"
    disable <== syncing
    onAction = _ => sync()
  }

  stage = new PrimaryStage {
    title = "ScalaFX Hello World"
    scene = new Scene {

      stylesheets.add(
        getClass.getClassLoader.getResource("customStyles.css").toString)

      root = new BorderPane {

        top = new MenuBar {
          useSystemMenuBar = true
          menus = Seq(
            new Menu {
              text = "File"
              items =
                if (Platform.current == Platform.MacPlatform)
                  Seq.empty
                else
                  Seq(
                    new MenuItem {
                      text = "Quit"
                      accelerator = new KeyCodeCombination(
                        KeyCode.Q,
                        KeyCombination.ShortcutDown
                      )
                      onAction = _ => JFXPlatform.exit()
                    }
                  )
            }
          )
        }

        center = new GridPane {
          padding = Insets(20.0)
          hgap = 5.0
          vgap = 2.0

          columnConstraints = Seq(
            defaulColumnContraints,
            new ColumnConstraints {
              hgrow = Always
              minWidth = 100.0
              prefWidth = 400.0
            },
            defaulColumnContraints,
          )
          private def defaulColumnContraints = new ColumnConstraints {
            hgrow = Never
          }

          rowConstraints = Seq(
            defaultRowConstraints,
            defaultRowConstraints,
            defaultRowConstraints,
            new RowConstraints {
              prefHeight = 10.0
              vgrow = Never
            },
            defaultRowConstraints,
          )
          private def defaultRowConstraints = new RowConstraints {
            vgrow = Never
          }

          GridPane.setConstraints(sourceLabel, 0, 0)
          GridPane.setConstraints(sourceTextField, 1, 0)
          GridPane.setConstraints(sourceButton, 2, 0)

          GridPane.setConstraints(targetLabel, 0, 1)
          GridPane.setConstraints(targetTextField, 1, 1)
          GridPane.setConstraints(targetButton, 2, 1)

          GridPane
            .setConstraints(syncButton, 1, 4, 2, 1, HPos.Right, VPos.Center)

          children = Seq(
            sourceLabel,
            sourceTextField,
            sourceButton,
            targetLabel,
            targetTextField,
            targetButton,
            syncButton,
          )
        }
      }
    }
  }

  private def directorySelectComponents(name: String) = {
    val path = ObjectProperty[Option[Path]](None)
    val label = new Label { text = s"$name:" }
    val textField = new TextField {
      editable = false
      disable = true
      text <== Bindings.createStringBinding(
        () => path.value.map(_.toString).getOrElse(""),
        path
      )
    }
    val button = new Button {
      text = "choose..."
      disable <== syncing
      onAction = _ => {
        val dirChooser = new DirectoryChooser {
          title = s"Pick a folder for the $name"
        }
        val choosenPath = Option {
          dirChooser.showDialog(stage.scene().getWindow)
        }.map(_.toPath)
        path.set(choosenPath)
        showMissingField(path, textField)
      }
    }
    (path, label, textField, button)
  }

  private def sync(): Unit = (sourcePath(), targetPath()) match {
    case (Some(source), Some(target)) =>
      doSync(source, target)
    case _ =>
      showMissingField(sourcePath, sourceTextField)
      showMissingField(targetPath, targetTextField)
  }

  private def doSync(source: Path, target: Path): Unit = {
    syncing.set(true)

    val config = GuiConfig(source, target)

    logger.info(s"Executing sync with conf: $config")
    SyncEngine(config).map(_.sync()) match {
      case Success(_) =>
        new Alert(AlertType.Information) {
          initOwner(stage)
          title = ""
          headerText = "Success!"
          contentText = "Target was successfuly synchronized with the library."
        }.showAndWait()
      case Failure(e) =>
        val errorText = new StringWriter
        e.printStackTrace(new PrintWriter(errorText))
        new Alert(AlertType.Error) {
          initOwner(stage)
          title = ""
          headerText = "Error"
          contentText = errorText.toString
        }.showAndWait()
    }

    syncing.set(false)
  }

  private def showMissingField[T](prop: ObjectProperty[Option[T]],
                                  field: TextInputControl): Unit = {
    if (prop().isEmpty) field.styleClass.add("error")
    else field.styleClass.remove("error")
  }

  override def stopApp(): Unit = {
    AppPreferences.lastSourcePath = sourcePath()
    AppPreferences.lastTargetPath = targetPath()
  }
}
