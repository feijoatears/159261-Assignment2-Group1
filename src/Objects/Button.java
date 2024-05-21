package src.Objects;


import src.GameEngine;
import src.GameEngine.AudioClip;
import java.awt.*;
import java.util.ArrayList;

public class Button extends Object
{
    //activated, deactivated imgs
    private final ArrayList<Image> images;
    private AudioClip onSound, offSound;


    public Button(int posX, int posY, ArrayList<AudioClip> sounds)
    {
        super(posX, posY, sounds);

        images = new ArrayList<>();
        images.add(GameEngine.loadImage("resources/Objects/buttonOn.png"));
        images.add(GameEngine.loadImage("resources/Objects/buttonOff.png"));
        this.image = images.getFirst();

        this.hitbox = new Rectangle(posX, posY, image.getHeight(null), image.getHeight(null));
    }

    public Image getCurrentImage()
    {
        return this.image;
    }

    public void activate()
    {
        this.image = images.get(1);
        isUsed = true;
    }
    public void deactivate()
    {
        this.image = images.getFirst();
        isUsed = false;
    }

    public AudioClip getOnSound()
    {
        return sounds.getFirst();
    }
    public AudioClip getOffSound()
    {
        return sounds.get(1);
    }
}
