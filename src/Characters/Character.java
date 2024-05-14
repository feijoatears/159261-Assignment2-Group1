package src.Characters;

import src.Direction;
import src.GameEngine;


import java.awt.*;

public class Character {
    protected int posX;
    protected int posY;
    protected Direction direction;
    protected Image image;
    boolean[] hasKey = new boolean[10];

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

    // Method to check if a specific key is present
    public boolean hasKey(int keyIndex) {
        if (keyIndex >= 0 && keyIndex < hasKey.length) {
            return hasKey[keyIndex];
        }
        return false;
    }

    // Method to set a key as collected
    public void collectKey(int keyIndex) {
        if (keyIndex >= 0 && keyIndex < hasKey.length) {
            hasKey[keyIndex] = true;
        }
    }
}
