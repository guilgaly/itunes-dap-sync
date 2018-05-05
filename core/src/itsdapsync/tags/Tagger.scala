package itsdapsync.tags

import java.nio.file.Path

import itsdapsync.ituneslib.ItunesTrack

trait Tagger {
  def replaceTags(targetFile: Path, itunesTrack: ItunesTrack): Unit
}
