package itsdapsync.codec

import java.nio.file.Path

import it.sauronsoftware.jave
import it.sauronsoftware.jave.{
  AudioAttributes,
  EncodingAttributes,
  FFMPEGLocator,
}

object FfmpegEncoder {
  def transcode(sourceFile: Path, targetFile: Path): Unit = {
    val encoder = new jave.Encoder(locator)
    encoder.encode(sourceFile.toFile, targetFile.toFile, encodingAttributes)
  }

  private val locator = new FFMPEGLocator {
    override def getFFMPEGExecutablePath: String =
      "/usr/local/opt/ffmpeg/bin/ffmpeg"
  }

  private val encodingAttributes = {
    val audio = new AudioAttributes
    audio.setCodec("aac")
    audio.setBitRate(160)

    val atr = new EncodingAttributes
    atr.setAudioAttributes(audio)
    atr.setFormat("m4a")
    atr
  }
}

object Test extends App {
  val encoder = new jave.Encoder(new FFMPEGLocator {
    override def getFFMPEGExecutablePath: String =
      "/usr/local/opt/ffmpeg/bin/ffmpeg"
  })

  println("Supported formats:")
  for (fmt <- encoder.getSupportedEncodingFormats) {
    println(s" - $fmt")
  }

  println()

  println("Audio encoders:")
  for (enc <- encoder.getAudioEncoders) {
    println(s" - $enc")
  }
}
