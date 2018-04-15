package itsdapsync.ituneslib

import java.net.URI
import java.nio.file.{Path, Paths}
import java.time.Instant

import com.dd.plist._

import scala.collection.JavaConverters._
import scala.language.implicitConversions

object NSObjectConverters {

  /**
   * Unsafe conversions (will throw [[java.lang.ClassCastException]]s when
   * attempting invalid conversions).
   */
  implicit class NsObjectToScala(val obj: NSObject) extends AnyVal {
    def toScalaMap: Map[String, NSObject] =
      obj.asInstanceOf[NSDictionary].asScala.toMap

    def toScalaSeq: Seq[NSObject] =
      obj.asInstanceOf[NSArray].getArray.toSeq

    def toScalaBoolean: Boolean =
      obj.asInstanceOf[NSNumber].boolValue()

    def toScalaInt: Int =
      obj.asInstanceOf[NSNumber].intValue()

    def toScalaString: String =
      obj.asInstanceOf[NSString].toString

    def toJavaInstant: Instant =
      obj.asInstanceOf[NSDate].getDate.toInstant

    def toJavaPath: Path = {
      val path = obj.asInstanceOf[NSString].toString
      // Java doesn't like URIs like "file://localhost/C:/foo" for accessing local
      // files: we need to remove the autority component and use something like
      // "file///C:/foo" instead
      val adaptedPath = path.replace("file://localhost", "file://")
      Paths.get(new URI(adaptedPath))
    }
  }
}
