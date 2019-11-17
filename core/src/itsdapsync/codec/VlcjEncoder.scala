package itsdapsync.codec

import java.nio.file.Path

import uk.co.caprica.vlcj.player.base.{MediaPlayer, MediaPlayerEventAdapter}
import uk.co.caprica.vlcj.player.component.AudioPlayerComponent

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Promise}
import scala.util.{Failure, Try}

class VlcjEncoder extends Encoder {
  override def transcode(sourceFile: Path, targetFile: Path): Unit = {
    val playerComponent = new AudioPlayerComponent()
    val player = playerComponent.mediaPlayer()

    val resultP = Promise[Unit]()
    player
      .events()
      .addMediaPlayerEventListener(new MediaPlayerEventAdapter {
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

    player
      .media()
      .play(
        sourceFile.toAbsolutePath.toString,
        "-I dummy",
        "--sout-vorbis-quality=5",
        transcodeOpts,
        "vlc://quit",
      )

    val result = Await.ready(resultP.future, Duration.Inf)
    result.value match {
      case Some(Failure(e)) =>
        println(s"the future failed with an error: $e")
      case _ => ()
    }
    player.release()
    playerComponent.release()
  }

  override def close(): Unit = {
    println("releasing component")
  }
}
