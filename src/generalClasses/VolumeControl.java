package src.generalClasses;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;
import javax.swing.*;
import java.awt.*;
import java.io.File;


public class VolumeControl
{
    private static VolumeControl instance;
    // Load sound files
    private final Clip keyCollectedSound = loadSound("resources/Sounds/Key.wav");

    // undo when needed too loud for rn
   private final Clip backgroundMusic =  loadSound("AHHHHHHHHresources/Sounds/C.wav");
    //Control Sound
    private final src.VolumeControl backgroundVolumeControl = new src.VolumeControl(backgroundMusic);

    public static VolumeControl getInstance()
    {
        if(instance == null)
        {
            instance = new VolumeControl();
        }
        return instance;
    }

    private VolumeControl()
    {
        JSlider volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        volumeSlider.addChangeListener(e -> {
            int value = volumeSlider.getValue();
            float volume = value / 100f;




           backgroundVolumeControl.setVolume(volume);
        });

        JFrame frame = new JFrame("Volume Control");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(volumeSlider, BorderLayout.CENTER);
        frame.setSize(300, 100);
        frame.setVisible(true);
    }

    /**
     * Loads a sound file.
     *
     * @param filepath The path to the sound file.
     * @return The loaded Clip object.
     */
    //.mp3 files do not work, .wav does
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

    public Clip getKeyCollectedSound()
    {
        return keyCollectedSound;
    }

    public Clip getBackgroundMusic()
    {
        return backgroundMusic;
    }
}
