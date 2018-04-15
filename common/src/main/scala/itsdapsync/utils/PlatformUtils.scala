package itsdapsync.utils

sealed trait Platform
case object MacPlatform extends Platform
case object LinuxPlatform extends Platform
case object WindowsPlatform extends Platform
case object UnknownPlatform extends Platform

object PlatformUtils {
  val platform: Platform = {
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
