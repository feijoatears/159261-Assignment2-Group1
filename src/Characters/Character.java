package src.Characters;

import src.Direction;
import src.GameEngine;


import java.awt.*;

public abstract class Character {
    protected int posX,
                  posY,
                  speed = 5;

    protected Direction direction;
    protected Image image;


    // empty constructor
    public Character() {}
    // Constructor
    public Character(int posX, int posY, Direction direction, Image image)
    {
        this.posX = posX;
        this.posY = posY;
        this.direction = direction;
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

    // Getters and setters for direction


    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {this.direction = direction;}

    public void setImage(String path)
    {
        this.image = GameEngine.loadImage(path);
    }
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


    public void handleWallCollision()
    {
        //makes inside area of map valid
        Rectangle validPosition = new Rectangle(0,0,450,450);
        if(this.posX > validPosition.width)
        {
            this.posX = validPosition.width;
        }
        else if(this.posX < 0)
        {
            this.posX = 0;
        }

        if(this.posY > validPosition.height)
        {
            this.posY = validPosition.height;
        }
        else if(this.posY < 0)
        {
            this.posY = 0;
        }
    }
}
