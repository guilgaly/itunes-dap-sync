package itsdapsync.experiments;

import uk.co.caprica.vlcj.binding.internal.libvlc_media_t;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventListener;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

public class VlcjTestMain {
    public static void main(String[] args) throws InterruptedException {
        Path sourceFile = Paths.get("C:\\Users\\Guillaume\\Desktop\\MP3 test files\\mp3sweeps-1f\\16Hz-20kHz-Exp-1f-5sec.mp3");
//        Path sourceFile = Paths.get("C:\\Users\\Guillaume\\Downloads\\MASTER BOOT RECORD - C-COPY -.- A- -V\\MASTER BOOT RECORD - C-COPY -.- A- -V - 01 RZR.NFO.mp3");
        Path targetFile = Paths.get("C:\\Users\\Guillaume\\Desktop\\test.ogg");

        new NativeDiscovery().discover();
        MediaPlayerFactory factory = new MediaPlayerFactory();
        MediaPlayer player = factory.newHeadlessMediaPlayer();

        CountDownLatch latch = new CountDownLatch(2);
        player.addMediaPlayerEventListener(new MediaPlayerEventListener() {
            @Override
            public void mediaChanged(final MediaPlayer mediaPlayer, final libvlc_media_t media, final String mrl) {
                System.out.println("mediaChanged");
            }

            @Override
            public void opening(final MediaPlayer mediaPlayer) {
                System.out.println("opening");
            }

            @Override
            public void buffering(final MediaPlayer mediaPlayer, final float newCache) {
                System.out.println("buffering");
            }

            @Override
            public void playing(final MediaPlayer mediaPlayer) {
                System.out.println("playing");
            }

            @Override
            public void paused(final MediaPlayer mediaPlayer) {
                System.out.println("paused");
            }

            @Override
            public void stopped(final MediaPlayer mediaPlayer) {
                System.out.println("stopped");
            }

            @Override
            public void forward(final MediaPlayer mediaPlayer) {
                System.out.println("forward");
            }

            @Override
            public void backward(final MediaPlayer mediaPlayer) {
                System.out.println("backward");
            }

            @Override
            public void finished(MediaPlayer mediaPlayer) {
                System.out.println("finished");
//                latch.countDown();
            }

            @Override
            public void timeChanged(final MediaPlayer mediaPlayer, final long newTime) {
//                System.out.println("timeChanged");
            }

            @Override
            public void positionChanged(final MediaPlayer mediaPlayer, final float newPosition) {
//                System.out.println("positionChanged");
            }

            @Override
            public void seekableChanged(final MediaPlayer mediaPlayer, final int newSeekable) {
                System.out.println("seekableChanged");
            }

            @Override
            public void pausableChanged(final MediaPlayer mediaPlayer, final int newPausable) {
                System.out.println("pausableChanged");
            }

            @Override
            public void titleChanged(final MediaPlayer mediaPlayer, final int newTitle) {
                System.out.println("titleChanged");
            }

            @Override
            public void snapshotTaken(final MediaPlayer mediaPlayer, final String filename) {
                System.out.println("snapshotTaken");
            }

            @Override
            public void lengthChanged(final MediaPlayer mediaPlayer, final long newLength) {
                System.out.println("lengthChanged");
            }

            @Override
            public void videoOutput(final MediaPlayer mediaPlayer, final int newCount) {
                System.out.println("videoOutput");
            }

            @Override
            public void scrambledChanged(final MediaPlayer mediaPlayer, final int newScrambled) {
                System.out.println("scrambledChanged");
            }

            @Override
            public void elementaryStreamAdded(final MediaPlayer mediaPlayer, final int type, final int id) {
                System.out.println("elementaryStreamAdded");
            }

            @Override
            public void elementaryStreamDeleted(final MediaPlayer mediaPlayer, final int type, final int id) {
                System.out.println("elementaryStreamDeleted");
            }

            @Override
            public void elementaryStreamSelected(final MediaPlayer mediaPlayer, final int type, final int id) {
                System.out.println("elementaryStreamSelected");
            }

            @Override
            public void corked(final MediaPlayer mediaPlayer, final boolean corked) {
                System.out.println("corked");
            }

            @Override
            public void muted(final MediaPlayer mediaPlayer, final boolean muted) {
                System.out.println("muted");
            }

            @Override
            public void volumeChanged(final MediaPlayer mediaPlayer, final float volume) {
                System.out.println("volumeChanged");
            }

            @Override
            public void audioDeviceChanged(final MediaPlayer mediaPlayer, final String audioDevice) {
                System.out.println("audioDeviceChanged");
            }

            @Override
            public void chapterChanged(final MediaPlayer mediaPlayer, final int newChapter) {
                System.out.println("chapterChanged");
            }

            @Override
            public void error(MediaPlayer mediaPlayer) {
                System.out.println("Rip failed");
                latch.countDown();
            }

            @Override
            public void mediaMetaChanged(final MediaPlayer mediaPlayer, final int metaType) {
                System.out.println("mediaMetaChanged");
            }

            @Override
            public void mediaSubItemAdded(final MediaPlayer mediaPlayer, final libvlc_media_t subItem) {
                System.out.println("mediaSubItemAdded");
            }

            @Override
            public void mediaDurationChanged(final MediaPlayer mediaPlayer, final long newDuration) {
                System.out.println("mediaDurationChanged");
            }

            @Override
            public void mediaParsedChanged(final MediaPlayer mediaPlayer, final int newStatus) {
                System.out.println("mediaParsedChanged");
            }

            @Override
            public void mediaParsedStatus(final MediaPlayer mediaPlayer, final int newStatus) {
                System.out.println("mediaParsedStatus");
            }

            @Override
            public void mediaFreed(final MediaPlayer mediaPlayer) {
                System.out.println("mediaFreed");
            }

            @Override
            public void mediaStateChanged(final MediaPlayer mediaPlayer, final int newState) {
                System.out.println("mediaStateChanged");
            }

            @Override
            public void mediaSubItemTreeAdded(final MediaPlayer mediaPlayer, final libvlc_media_t item) {
                System.out.println("mediaSubItemTreeAdded");
            }

            @Override
            public void newMedia(final MediaPlayer mediaPlayer) {
                System.out.println("newMedia");
                latch.countDown();
            }

            @Override
            public void subItemPlayed(final MediaPlayer mediaPlayer, final int subItemIndex) {
                System.out.println("subItemPlayed");
            }

            @Override
            public void subItemFinished(final MediaPlayer mediaPlayer, final int subItemIndex) {
                System.out.println("subItemFinished");
            }

            @Override
            public void endOfSubItems(final MediaPlayer mediaPlayer) {
                System.out.println("endOfSubItems");
            }
        });

        String transcodeOpts =
                ":sout=#transcode{acodec=vorb,vcodec=dummy}:std{dst=" + targetFile.toAbsolutePath().toString() + ",mux=ogg,access=file}";
        player.playMedia(
                sourceFile.toAbsolutePath().toString(),
                transcodeOpts);

        latch.await();
        System.out.println("Finished!");

        Thread.sleep(1000);

        player.release();
        System.out.println("Player released");

        factory.release();
        System.out.println("Factory released");
    }
}
