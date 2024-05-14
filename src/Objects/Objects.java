package src.Objects;

import java.awt.*;

public class Objects
{
    protected int posX, posY;
    protected boolean isInteractable, isUsed;
    protected Image image;

    public Objects(){};

    public int getPosX()
    {
        return posX;
    }
    public void setPosX(int posX)
    {
        this.posX = posX;
    }

    public int getPosY()
    {
        return posY;
    }
    public void setPosY(int posY)
    {
        this.posY = posY;
    }

    public void setInteractable(boolean interactable)
    {
        isInteractable = interactable;
    }
    public boolean getIsInteractable()
    {
        return isInteractable;
    }

    public void setUsed(boolean used)
    {
        isUsed = used;
    }
    public boolean getIsUsed()
    {
        return isUsed;
    }
}