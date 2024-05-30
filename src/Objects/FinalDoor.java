package src.Objects;

import java.awt.*;

public class FinalDoor extends Object{
    private Image image;
    private int posX, posY;

    public FinalDoor(Image image, int posX, int posY) {
        this.image = image;
        this.posX = posX;
        this.posY = posY;
    }

    public Image getImage() {
        return image;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public Rectangle getHitbox() {
        return new Rectangle(posX, posY, image.getWidth(null), image.getHeight(null));
    }
}

