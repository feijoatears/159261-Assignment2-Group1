package src;

import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Clip;

public class VolumeControl {
    private FloatControl volumeControl;

    public VolumeControl(Clip clip) {
        if (clip != null && clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        }
    }

    public void setVolume(float volume) {
        if (volumeControl != null) {
            float min = volumeControl.getMinimum();
            float max = volumeControl.getMaximum();
            float value = min + (volume * (max - min));
            volumeControl.setValue(value);
        }
    }
}
