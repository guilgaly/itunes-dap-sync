package itsdapsync.utils

import enumeratum.{Enum, EnumEntry}

import scala.collection.immutable.IndexedSeq

sealed trait Platform extends EnumEntry

object Platform extends Enum[Platform] {
  case object MacPlatform extends Platform
  case object LinuxPlatform extends Platform
  case object WindowsPlatform extends Platform
  case object UnknownPlatform extends Platform

  override def values: IndexedSeq[Platform] = findValues

  def current: Platform = {
    val osName = System.getProperty("os.name")

    if (osName.startsWith("Mac"))
      MacPlatform
    else if (osName.startsWith("Linux"))
      LinuxPlatform
    else if (osName.startsWith("Windows"))
      WindowsPlatform
    else
      UnknownPlatform
  }
}
