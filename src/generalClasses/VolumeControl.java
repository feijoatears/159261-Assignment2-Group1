package src.generalClasses;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.FloatControl;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class VolumeControl {
    private static VolumeControl instance;
    private static FloatControl volumeControl;

    // Load sound files
    private final Clip keyCollectedSound = loadSound("resources/Sounds/Key.wav");
    private final Clip attackSound = loadSound("resources/Sounds/Attack.wav");
    private final Clip damageSound = loadSound("resources/Sounds/damage.wav");
    private final Clip wowSound = loadSound("resources/Sounds/wow.wav");
    private final Clip backgroundMusic = loadSound("resources/Sounds/C.wav");

    public static VolumeControl getInstance() {
        if (instance == null) {
            instance = new VolumeControl();
        }
        return instance;
    }

    private VolumeControl() {
        // Initialize volume control for background music
        if (backgroundMusic != null && backgroundMusic.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            volumeControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
        }
    }

    public static void setVolume(float volume) {
        if (volumeControl != null) {
            float min = volumeControl.getMinimum();
            float max = volumeControl.getMaximum();
            float value = min + (volume * (max - min));
            volumeControl.setValue(value);

        }
    }

    /**
     * Loads a sound file.
     *
     * @param filepath The path to the sound file.
     * @return The loaded Clip object.
     */
    public Clip loadSound(String filepath) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filepath));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            return clip;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Clip getKeyCollectedSound() {
        return keyCollectedSound;
    }

    public Clip getAttackSound() {
        return attackSound;
    }

    public Clip getDamageSound() {
        return damageSound;
    }

    public Clip getWowSound() {
        return wowSound;
    }

    public Clip getBackgroundMusic() {
        return backgroundMusic;
    }

    public void setBackgroundMusic() {
        instance = null;
    }

    public void reset() {
        // Reset volume control state
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.setFramePosition(0);
        }
        if (keyCollectedSound != null) {
            keyCollectedSound.stop();
            keyCollectedSound.setFramePosition(0);
        }
        if (attackSound != null) {
            attackSound.stop();
            attackSound.setFramePosition(0);
        }
        if (damageSound != null) {
            damageSound.stop();
            damageSound.setFramePosition(0);
        }
        if (wowSound != null) {
            wowSound.stop();
            wowSound.setFramePosition(0);
        }
    }
}
