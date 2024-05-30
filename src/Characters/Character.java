package src.Characters;

import src.Direction;
import src.GameEngine;
import src.generalClasses.*;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Character
{
    protected int posX,
                  posY,
                  width,
                  height,
                  speed = 5;

    protected Direction direction;

    protected Image spriteSheet,
                    image;

    protected Image[] frames = new Image[]{};

    protected Rectangle hitbox;

    // empty constructor
    public Character() {}

    public Character(int posX, int posY, int speed, Image spriteSheet, int numFrames)
    {
        this.posX = posX;
        this.posY = posY;
        this.speed = speed;
        this.spriteSheet = spriteSheet;

        int frameWidth = spriteSheet.getWidth(null) / numFrames,
            frameHeight = spriteSheet.getHeight(null);

        frames = new Image[numFrames];

        for (int i = 0; i < numFrames; i++)
        {

            frames[i] = subImage(spriteSheet, i * frameWidth, 0 , frameWidth, frameHeight);
        }
        image = (frames[0]);

        this.width = 20;
        this.height = image.getHeight(null);
        this.hitbox = new Rectangle(width, height);
    }

    private Image subImage(Image source, int x, int y, int w, int h)
    {
        if(source == null)
        {
            System.out.println("Error: cannot extract a subImage from a null image.\n");
            return null;
        }

        BufferedImage buffered = (BufferedImage)source;

        return buffered.getSubimage(x, y, w, h);
    }

    // Getters and setters for posX
    public int getPosX() {
        return posX;
    }
    public void setPosX(int posX) {
        this.posX = posX;
    }

    // Getters and setters for posY
    public int getPosY() {
        return posY;
    }
    public void setPosY(int posY) {
        this.posY = posY;
    }

    public void setWidth(int width) { this.width = width; }
    public int getWidth() { return width; }

    public void setHeight(int height) { this.height = height; }
    public int getHeight() { return height; }

    // Getters and setters for direction
    public Direction getDirection() {
        return direction;
    }
    public void setDirection(Direction direction) {this.direction = direction;}

    public void setImage(String path) { this.image = GameEngine.loadImage(path); }
    public Image getImage()
    {
        return image;
    }

    public int getSpeed()
    {
        return speed;
    }
    public void setSpeed(int speed)
    {
        this.speed = speed;
    }

    public void setHitbox(Rectangle hitbox) { this.hitbox = hitbox; }
    public Rectangle getHitbox() { return hitbox; }

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
        Rectangle newHitbox = new Rectangle(this.posX + 15, this.posY, this.width, this.height);
        if (!level.isPositionClear(newHitbox))
        {
            // Revert to the last valid position
            this.posX = previousPosX;
            this.posY = previousPosY;
        }

        // Update the hitbox position
        this.hitbox.x = newHitbox.x;
        this.hitbox.y = newHitbox.y;
    }

}
