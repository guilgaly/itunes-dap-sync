package itsdapsync.json

import java.time.Instant

import upickle.AttributeTagged

object CustomPickler extends AttributeTagged {

  type RW[T] = ReadWriter[T]

  implicit val instantReader: Reader[Instant] =
    new MapStringReader[Instant](Instant.parse)

  implicit val instantWriter: Writer[Instant] =
    StringWriter.comap[Instant](_.toString)
}
