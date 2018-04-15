package itsdapsync.codec

import java.nio.file.Path
import java.util

import itsdapsync.codec.VlcjEncoder._
import uk.co.caprica.vlcj.discovery.NativeDiscovery
import uk.co.caprica.vlcj.discovery.linux.DefaultLinuxNativeDiscoveryStrategy
import uk.co.caprica.vlcj.discovery.mac.DefaultMacNativeDiscoveryStrategy
import uk.co.caprica.vlcj.discovery.windows.DefaultWindowsNativeDiscoveryStrategy
import uk.co.caprica.vlcj.player.{
  MediaPlayer,
  MediaPlayerEventAdapter,
  MediaPlayerFactory
}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Promise}
import scala.util.{Failure, Try}

object VlcjEncoder {
  private class ImprovedMacNativeDiscoveryStrategy
      extends DefaultMacNativeDiscoveryStrategy {
    override def onGetDirectoryNames(
        directoryNames: util.List[String]): Unit = {
      super.onGetDirectoryNames(directoryNames)
      directoryNames.add("/Applications/Media/VLC.app/Contents/MacOS/lib")
      directoryNames.add("/Applications/Video/VLC.app/Contents/MacOS/lib")
    }
  }

  private def nativeDiscovery =
    new NativeDiscovery(new ImprovedMacNativeDiscoveryStrategy,
                        new DefaultLinuxNativeDiscoveryStrategy,
                        new DefaultWindowsNativeDiscoveryStrategy)
}

class VlcjEncoder extends Encoder {

  if (!nativeDiscovery.discover()) {
    throw new IllegalStateException("failed to discover native VLC library")
  }

  private[this] val playerFactory = new MediaPlayerFactory

  override def transcode(sourceFile: Path, targetFile: Path): Unit = {
    val player = playerFactory.newHeadlessMediaPlayer

    val resultP = Promise[Unit]()
    player.addMediaPlayerEventListener(new MediaPlayerEventAdapter {
      override def finished(mediaPlayer: MediaPlayer): Unit = {
        println("finished")
        resultP.complete(Try {})
      }

      override def error(mediaPlayer: MediaPlayer): Unit = {
        println("error")
        resultP.failure(new Exception("Transcoding failed"))
      }
    })

    val transcodeOpts =
      s""":sout=#transcode{acodec=vorb,vcodec=dummy}:std{dst="${targetFile.toAbsolutePath}",mux=ogg,access=file}"""

    player.playMedia(
      sourceFile.toAbsolutePath.toString,
      "-I dummy",
      "--sout-vorbis-quality=5",
      transcodeOpts,
      "vlc://quit"
    )

    val result = Await.ready(resultP.future, Duration.Inf)
    result.value match {
      case Some(Failure(e)) =>
        println(s"the future failed with an error: $e")
      case _ => ()
    }
    player.release()
  }

  override def close(): Unit = {
    if (playerFactory != null) {
      println("releasing component")
      playerFactory.release()
    }
  }
}
