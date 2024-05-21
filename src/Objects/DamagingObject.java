package src.Objects;

import src.Characters.Player;

import java.awt.*;

public class DamagingObject extends Object
{
    public DamagingObject(Image image, int posX, int posY, int width, int height)
    {
        super(posX, posY, image);
        this.width = width;
        this.height = height;
        this.hitbox = new Rectangle(posX, posY, width, height);
    }
}
