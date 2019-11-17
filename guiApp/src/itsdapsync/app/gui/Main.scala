package itsdapsync.app.gui

import javafx.{scene => jfxs}
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafxml.core.{FXMLLoader, NoDependencyResolver}

object Main extends JFXApp {
  private val loader =
    new FXMLLoader(getClass.getResource("main.fxml"), NoDependencyResolver)

  private val root = loader.load[jfxs.Parent]()
  private val mainController = loader.getController[MainControllerInterface]

  stage = new JFXApp.PrimaryStage() {
    title = "iTunes DAP Sync"
    scene = new Scene(root)
  }

  override def stopApp(): Unit = {
    mainController.onStop()
  }
}
