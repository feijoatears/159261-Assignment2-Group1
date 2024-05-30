package src.Objects;

import java.awt.*;

import static src.GameEngine.loadImage;

public class Key extends Object {
    private int id;
    private boolean isUsed;

    public Key(int posX, int posY) {
        super(posX, posY, loadImage("resources/Objects/newKey.png"));
        this.isInteractable = true;
        this.isUsed = false;
    }

    public boolean checkCollision(Rectangle playerRect) {
        Rectangle keyRect = new Rectangle(this.posX, this.posY, this.image.getWidth(null), this.image.getHeight(null));
        return playerRect.intersects(keyRect);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(boolean isUsed) {
        this.isUsed = isUsed;
    }
}

