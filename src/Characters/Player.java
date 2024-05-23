package src.Characters;

import src.Direction;
import src.GameEngine;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static src.GameEngine.*;



public class Player extends Character
{
    private static Player instance;

    protected Image playerSpriteSheet = loadImage("resources/Sprites/Player-spritesheet.png");
    private final Image heartImage = loadImage("resources/Objects/heart.png");

    private final Image[] humanFrames;

    private final int[] eastFrames = {0, 1},
                        southFrames = {2, 3},
                       damageFrames = {4, 5},
                        westFrames = {8, 9},
                        northFrames = {6, 7};

    private int speed = 5,
                lives = 3,
                currentFrameIndex = 0;

    private static Thread thread;

    public void reset()
    {
        lives = 3;
    }
    private Player()
    {
        int numFrames = 10,
            frameWidth = playerSpriteSheet.getWidth(null) / numFrames,
            frameHeight = playerSpriteSheet.getHeight(null);

        humanFrames = new Image[numFrames];

        for (int i = 0; i < numFrames; i++)
        {

            humanFrames[i] = subImage(playerSpriteSheet, i * frameWidth, 0 , frameWidth, frameHeight);
        }
        setImage(humanFrames[0]);
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

    public boolean[] hasKey = new boolean[10];
    private boolean isMoving = false;

    public static Player getInstance()
    {
        if(instance == null)
        {
            instance = new Player();
        }
        return instance;
    }

    public void setImage(Image image) {
        this.image = image;
    }    

    public void move(ArrayList<Rectangle> walls)
    {

        int nextPosX = posX;
        int nextPosY = posY;


        //unique to player bc controllable, other sprites will move in a pre-programmed manner
        switch (this.getDirection())
        {
            case North:
            {
                //this.setPosY(posY - speed);
                nextPosY = posY - speed;
                break;
            }
            case South:
            {
                //this.setPosY(posY + speed);
                nextPosY = posY + speed;
                break;
            }
            case West:
            {
                //this.setPosX(posX - speed);
                nextPosX = posX - speed;
                break;
            }
            case East:
            {
                //this.setPosX(posX + speed);
                nextPosX = posX + speed;
                break;
            }
            case Northwest:
            {
                //this.setPosY(posY - speed);
                //this.setPosX(posX - speed);
                nextPosY = posY - speed;
                nextPosX = posX - speed;
                break;
            }
            case Northeast:
            {
                //this.setPosY(posY - speed);
                //this.setPosX(posX + speed);
                nextPosY = posY - speed;
                nextPosX = posX + speed;
                break;
            }
            case Southwest:
            {
                //this.setPosY(posY + speed);
                //this.setPosX(posX - speed);
                nextPosY = posY + speed;
                nextPosX = posX - speed;
                break;
            }
            case Southeast:
            {
                //this.setPosY(posY + speed);
                //this.setPosX(posX + speed);
                nextPosY = posY + speed;
                nextPosX = posX + speed;
                break;
            }
        }

        Rectangle nextRect = new Rectangle(nextPosX, nextPosY, image.getWidth(null), image.getHeight(null));
        for (Rectangle wall : walls) {
            if (nextRect.intersects(wall)) {
                return; // Collision detected, don't move the player
            }
        }

        this.setPosX(nextPosX);
        this.setPosY(nextPosY);

        if (nextPosX < 0 || nextPosY < 0 || nextPosX + image.getWidth(null) > 500 || nextPosY + image.getHeight(null) > 500) {
            return; // Outside screen boundaries, don't move the player
        }


        if (direction == Direction.East)
        {
            currentFrameIndex = (currentFrameIndex + 1) % eastFrames.length;
            image = (humanFrames[eastFrames[currentFrameIndex]]);
        }
        else if (direction == Direction.West)
        {
            currentFrameIndex = (currentFrameIndex + 1) % westFrames.length;
            image = (humanFrames[westFrames[currentFrameIndex]]);
        }
        else if (direction == Direction.North ||
                direction == Direction.Northeast ||
                direction == Direction.Northwest)
        {
            currentFrameIndex = (currentFrameIndex + 1) % northFrames.length;
            image = (humanFrames[northFrames[currentFrameIndex]]);
        }
        else
        {
            currentFrameIndex = (currentFrameIndex + 1) % southFrames.length;
            image = (humanFrames[southFrames[currentFrameIndex]]);
        }
    }

    public boolean checkCollision(Rectangle other)
    {
        Rectangle playerRect = new Rectangle(this.posX, this.posY, this.image.getWidth(null), this.image.getHeight(null));
        return playerRect.intersects(other);
    }
    public int getSpeed()
    {
        return speed;
    }

    public void setSpeed(int speed)
    {
        this.speed = speed;
    }

    // Method to check if a specific key is present
    public boolean hasKey(int keyNum) {
        if (keyNum >= 0 && keyNum < hasKey.length) {
            return hasKey[keyNum];
        }
        return false;
    }

    // Method to set a key as collected
    public void collectKey(int keyNum) {
        if (keyNum >= 0 && keyNum < hasKey.length) {
            hasKey[keyNum] = true;
        }
    }

    public boolean isMoving()
    {
        return isMoving;
    }
    public void setMoving(boolean moving)
    {
        isMoving = moving;
    }

    //life functions
    public Image getHeartImage()
    {
        return heartImage;
    }
    public int getLives()
    {
        return lives;
    }

    public synchronized void damage(Rectangle other)
    {
        if (checkCollision(other))
        {
            //starts a thread that checks every two seconds if player is on a damaging object
            //if they are, player loses a life, stop thread if not
            //adds iFrames so player doesn't immediately die when they touch a damaging object
            if(thread == null || !thread.isAlive())
            {
                thread = new Thread( () ->
                {
                    try
                    {
                        lives -= 1;
                        Thread.sleep(2000);
                        currentFrameIndex = (currentFrameIndex + 1) % damageFrames.length;
                        image = (humanFrames[damageFrames[currentFrameIndex]]);                    }
                    catch (InterruptedException e)
                    {
                        //player leaves damaging object hitbox
                        Thread.currentThread().interrupt();
                    }
                });
                thread.start();
            }
        }
        else
        {
            if(thread != null)
            {
                thread.interrupt();
            }
        }
    }
    public void gainLife()
    {
        lives += 1;
    }

    public void bounceBack(Direction direction, int steps) {
        int stepSize = 10; // Define the size of each step 
        switch (direction) {
            case North:
                setPosY(getPosY() + stepSize * steps);
                break;
            case South:
                setPosY(getPosY() - stepSize * steps);
                break;
            case East:
                setPosX(getPosX() - stepSize * steps);
                break;
            case West:
                setPosX(getPosX() + stepSize * steps);
                break;
            case Northeast:
                setPosX(getPosX() - stepSize * steps);
                setPosY(getPosY() + stepSize * steps);
                break;
            case Northwest:
                setPosX(getPosX() + stepSize * steps);
                setPosY(getPosY() + stepSize * steps);
                break;
            case Southeast:
                setPosX(getPosX() - stepSize * steps);
                setPosY(getPosY() - stepSize * steps);
                break;
            case Southwest:
                setPosX(getPosX() + stepSize * steps);
                setPosY(getPosY() - stepSize * steps);
                break;
        }
    }

    public void attack(ArrayList<Enemy> enemies) {
        Rectangle attackRange = new Rectangle(getPosX() - 10, getPosY() - 10, getImage().getWidth(null) + 20, getImage().getHeight(null) + 20);
        for (Enemy enemy : enemies) {
            if (attackRange.intersects(enemy.getHitbox())) {
                // Damage the enemy or remove it
                // Example: Remove enemy for simplicity
                enemies.remove(enemy);
                break;
            }
        }
    }
}
