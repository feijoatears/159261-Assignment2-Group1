package src.Characters;

import src.Direction;
import src.generalClasses.Level;

import java.awt.*;

import static src.GameEngine.loadImage;

public abstract class Character {
    protected int posX, posY, width, height, speed;
    protected Direction direction;
    protected Image image;
    protected Image[] frames;
    protected Rectangle hitbox;

    public Character(int posX, int posY, int speed, String frameString, int numFrames) {
        this.posX = posX;
        this.posY = posY;
        this.speed = speed;


        frames = new Image[numFrames];
        for (int i = 0; i < numFrames; i++) {
            frames[i] = loadImage(frameString + (i+1) + ".png");
        }
        this.image = (frames[0]);

        assert image != null;
        this.width = image.getWidth(null);
        this.height = image.getHeight(null);
        this.hitbox = new Rectangle(posX, posY, width, height); // Smaller hitbox
    }

    // Getters and setters for posX
    public int getPosX() {
        return posX;
    }
    public void setPosX(int posX) {
        this.posX = posX;
        updateHitbox();
    }

    // Getters and setters for posY
    public int getPosY() {
        return posY;
    }
    public void setPosY(int posY) {
        this.posY = posY;
        updateHitbox();
    }

    public void setWidth(int width) {
        this.width = width;
    }
    public int getWidth() {
        return width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
    public int getHeight() {
        return height;
    }

    // Getters and setters for direction
    public Direction getDirection() {
        return direction;
    }
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setImage(String path) {
        this.image = loadImage(path);
    }
    public Image getImage() {
        return image;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public void updateHitbox() {
        this.hitbox = new Rectangle(posX, posY, width, height);
    }

    public void handleWallCollision(Level level) {
        // Store the previous position
        int previousPosX = this.posX;
        int previousPosY = this.posY;

        // Adjust position if outside valid area
        Rectangle validPosition = new Rectangle(0, 0, 450, 450);
        if (this.posX > validPosition.width) {
            this.posX = validPosition.width;
        } else if (this.posX < 0) {
            this.posX = 0;
        }
        if (this.posY > validPosition.height) {
            this.posY = validPosition.height;
        } else if (this.posY < 0) {
            this.posY = 0;
        }

        // Check for collisions with obstacles
        Rectangle newHitbox = new Rectangle(this.posX, this.posY, this.hitbox.width, hitbox.height);


        if (!level.isPositionClear(newHitbox)) {
            // Revert to the last valid position
            this.posX = previousPosX;
            this.posY = previousPosY;
        }

        // Update the hitbox position
        updateHitbox();
    }

}
