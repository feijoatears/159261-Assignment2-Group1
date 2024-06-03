package src.Objects;

import src.GameEngine;

import java.awt.*;
import java.util.ArrayList;

public abstract class Object
{
    protected int posX, posY, width, height;
    protected boolean isInteractable, isUsed;
    protected Image image;
    protected ArrayList<GameEngine.AudioClip> sounds;
    protected Rectangle hitbox;

    public Object(int posX, int posY, int width, int height)
    {
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;

        this.hitbox = new Rectangle(posX,posY,width,height);
    }
    public Object(int posX, int posY, ArrayList<GameEngine.AudioClip> sounds)
    {
        this.posX = posX;
        this.posY = posY;
        this.sounds = sounds;
    }
    public Object(int posX, int posY, Image image)
    {
        this.posX = posX;
        this.posY = posY;
        this.image = image;

        this.width = image.getWidth(null);
        this.height = image.getHeight(null);
    }

    public Image getImage()
    {
        return image;
    }
    public void setImage(Image image)
    {
        this.image = image;
    }

    public int getPosX()
    {
        return posX;
    }
    public int getPosY()
    {
        return posY;
    }

    public Rectangle getHitbox()

    {
        return hitbox;
    }

    public int getWidth()
    {
        return width;
    }
    public int getHeight()
    {
        return height;
    }
}