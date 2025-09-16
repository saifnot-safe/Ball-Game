package Utils;

import javafx.scene.media.AudioClip;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * SoundManager.java
 * Handles game sound effects
 */

public class SoundManager {
    public final List<AudioClip> chimes = Arrays.asList(
            new AudioClip(Objects.requireNonNull(getClass().getResource("/chime.wav")).toExternalForm()),
            new AudioClip(Objects.requireNonNull(getClass().getResource("/chime2.wav")).toExternalForm()),
            new AudioClip(Objects.requireNonNull(getClass().getResource("/chime3.wav")).toExternalForm()),
            new AudioClip(Objects.requireNonNull(getClass().getResource("/chime4.wav")).toExternalForm()),
            new AudioClip(Objects.requireNonNull(getClass().getResource("/chime5.wav")).toExternalForm()),
            new AudioClip(Objects.requireNonNull(getClass().getResource("/chime6.wav")).toExternalForm()),
            new AudioClip(Objects.requireNonNull(getClass().getResource("/chime7.wav")).toExternalForm())


    );

    public void playChime(int chimeIndex) {
        chimes.get(chimeIndex).play();
    }
}
