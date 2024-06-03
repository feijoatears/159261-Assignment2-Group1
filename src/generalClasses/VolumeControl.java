package src.generalClasses;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.FloatControl;
import java.io.File;

public class VolumeControl {
    private static VolumeControl instance;
    private FloatControl bgVolumeControl;
    private FloatControl keyVolumeControl;
    private FloatControl attackVolumeControl;
    private FloatControl damageVolumeControl;
    private FloatControl wowVolumeControl;

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
            bgVolumeControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
        }
        if (keyCollectedSound != null && keyCollectedSound.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            keyVolumeControl = (FloatControl) keyCollectedSound.getControl(FloatControl.Type.MASTER_GAIN);
        }
        if (attackSound != null && attackSound.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            attackVolumeControl = (FloatControl) attackSound.getControl(FloatControl.Type.MASTER_GAIN);
        }
        if (damageSound != null && damageSound.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            damageVolumeControl = (FloatControl) damageSound.getControl(FloatControl.Type.MASTER_GAIN);
        }
        if (wowSound != null && wowSound.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            wowVolumeControl = (FloatControl) wowSound.getControl(FloatControl.Type.MASTER_GAIN);
        }
    }

    public void setBgVolume(float volume) {
        setVolume(bgVolumeControl, volume);
    }

    public void setKeyVolume(float volume) {
        setVolume(keyVolumeControl, volume);
    }

    public void setAttackVolume(float volume) {
        setVolume(attackVolumeControl, volume);
    }

    public void setDamageVolume(float volume) {
        setVolume(damageVolumeControl, volume);
    }

    public void setWowVolume(float volume) {
        setVolume(wowVolumeControl, volume);
    }

    public void setMasterVolume(float volume) {
        setBgVolume(volume);
        setKeyVolume(volume);
        setAttackVolume(volume);
        setDamageVolume(volume);
        setWowVolume(volume);
    }

    private void setVolume(FloatControl volumeControl, float volume) {
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
            System.out.println("Couldn't load sound: " + e.getMessage());
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
