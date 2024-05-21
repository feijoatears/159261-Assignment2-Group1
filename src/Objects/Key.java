package src.Objects;

import java.awt.*;

public class Key extends Object
{
    private int id;

    public Key(int posX, int posY, Image keyImage)
    {
        super(posX, posY, keyImage);
        this.isInteractable = true;
        this.isUsed = false;
    }

    public boolean checkCollision(Rectangle playerRect) {
        Rectangle keyRect = new Rectangle(this.posX, this.posY, this.image.getWidth(null), this.image.getHeight(null));
        return playerRect.intersects(keyRect);
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }
}
