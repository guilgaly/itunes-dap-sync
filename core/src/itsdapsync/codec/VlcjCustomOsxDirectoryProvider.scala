package itsdapsync.codec

import uk.co.caprica.vlcj.binding.RuntimeUtil
import uk.co.caprica.vlcj.factory.discovery.provider.DiscoveryDirectoryProvider

class VlcjCustomOsxDirectoryProvider extends DiscoveryDirectoryProvider {

  override def priority(): Int = 1

  override def directories(): Array[String] = Array(
    "/Applications/Media/VLC.app/Contents/MacOS/lib",
    "/Applications/Video/VLC.app/Contents/MacOS/lib",
  )

  override def supported(): Boolean = RuntimeUtil.isMac
}
