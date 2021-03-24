import Engine.SoundEffect;

public class SoundEffectFactory {

    public static SoundEffect throwCard() {
        return new SoundEffect("Throwing.wav");
    }

    public static SoundEffect shuffle() {
        return new SoundEffect("Shuffling.wav");
    }

    public static SoundEffect music() {
        return new SoundEffect("MainTheme.wav");
    }

    public static SoundEffect death() {
        return new SoundEffect("DeathSound.wav");
    }

    public static SoundEffect murloc() {
        return new SoundEffect("Murloc.wav");
    }
}
