import $file.dependencies
import $file.settings
import mill._
import mill.scalalib._
import mill.scalalib.scalafmt.ScalafmtModule

trait FormattedScalaModule extends ScalaModule with ScalafmtModule {
  // We need to override this (not just 'scalafmtVersion') because the group ID has changed
  override def scalafmtDeps: T[Agg[PathRef]] = T {
    Lib.resolveDependencies(
      zincWorker.repositories,
      Lib.depToDependency(_, "2.12.8"),
      Seq(ivy"org.scalameta::scalafmt-cli:2.0.0-RC5"),
    )
  }
}

trait itsDapSyncModule extends FormattedScalaModule {
  override def scalaVersion = settings.scalaVersion
  override def scalacOptions = settings.defaultScalacOptions
  override def repositories = super.repositories ++ settings.customRepositories

  object test extends Tests with FormattedScalaModule {
    override def moduleDeps =
      if (this == common.test) super.moduleDeps
      else super.moduleDeps ++ Seq(common.test)
    override def ivyDeps = Agg(dependencies.scalatest)
    override def testFrameworks = Seq("org.scalatest.tools.Framework")
  }
}

object cmdLineApp extends itsDapSyncModule {
  override def moduleDeps = Seq(core)
  override def ivyDeps = Agg(
    dependencies.scopt,
  )
}

object guiApp extends itsDapSyncModule {
  override def moduleDeps = Seq(core)
  override def ivyDeps = Agg(
    dependencies.scalafx.jfxFxml,
    dependencies.scalafx.jfxMedia,
    dependencies.scalafx.core,
    dependencies.scalafx.fxml,
  )
  override def scalacPluginIvyDeps = Agg(
    dependencies.compilerPlugins.macroParadise,
  )
}

object core extends itsDapSyncModule {
  override def moduleDeps = Seq(ituneslib)
  override def ivyDeps = Agg(
    dependencies.jaudiotagger,
    dependencies.vlcj,
    dependencies.jave,
  )
}

object experiments extends itsDapSyncModule {
  override def moduleDeps = Seq(core)
}

object ituneslib extends itsDapSyncModule {
  override def moduleDeps = Seq(common)
  override def ivyDeps = Agg(
    dependencies.ddPlist,
  )
}

object common extends itsDapSyncModule {
  override def ivyDeps = Agg(
    dependencies.logging.log4s,
    dependencies.logging.slf4jApi,
    dependencies.logging.slf4jSimple,
    dependencies.playJson,
    dependencies.enumeratum,
  )
}
