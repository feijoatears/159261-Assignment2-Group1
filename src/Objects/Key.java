package src.Objects;

import java.awt.*;

public class Key extends Objects {
    public Key(int posX, int posY, Image image) {
        this.posX = posX;
        this.posY = posY;
        this.image = image;
        this.isInteractable = true;
        this.isUsed = false;
    }

    public boolean checkCollision(Rectangle playerRect) {
        Rectangle keyRect = new Rectangle(this.posX, this.posY, this.image.getWidth(null), this.image.getHeight(null));
        return playerRect.intersects(keyRect);
    }
}