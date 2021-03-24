package Engine;

import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundEffect {

    public static final String BASE_PATH = "audios/";
    public static enum Volume {
        MUTE, LOW, MEDIUM, HIGH
    }

    public Volume volume = Volume.HIGH;
    private Clip clip;

    public SoundEffect(String filename) {
        try {
            URL url = this.getClass().getClassLoader().getResource(BASE_PATH + filename);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public final void setVolume(Volume volume) {
        this.volume = volume;
    }

    public final void playWithoutInterrupt() {
        play(false, false);
    }

    public final void play() {
        play(false, true);
    }

    public final void loop() {
        play(true, false);
    }

    private void play(boolean loop, boolean allowInterrupt) {
        if (volume != Volume.MUTE) {
            if (clip.isRunning() && allowInterrupt) {
                clip.stop();
            } else if (clip.isRunning()) {
                return;
            }

            clip.setFramePosition(0);
            clip.start();
            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
        }
    }

    public final boolean isRunning() {
        return clip.isRunning();
    }

    public final void stop() {
        clip.stop();
        clip.setFramePosition(0);
    }

    public final void mute() {
        volume = Volume.MUTE;
    }
}