import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

/**
 * Code I copied from Mr.McKenzie, but I added the loop method and added on to
 * the stop method, the loop code I copied from here:
 * https://www.tabnine.com/code/java/methods/javax.sound.sampled.Clip/loop.
 */
public class SoundEffect {
    private Clip c;

    public SoundEffect(String filename) {
        setClip(filename);
    }

    public void setClip(String filename) {
        try {
            File f = new File(filename);
            c = AudioSystem.getClip();
            c.open(AudioSystem.getAudioInputStream(f));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play() {
        c.setFramePosition(0);
        c.start();
    }

    public void loop() {
        c.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stop() {
        c.stop();
        c.setFramePosition(0);
    }
}
