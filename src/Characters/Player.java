package src.Characters;

import src.Direction;


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

    private int lives = 3,
                currentFrameIndex = 0;

    private static Thread iFrameThread,
                          bouncyThread;

    public boolean[] hasKey = new boolean[10];
    private boolean isMoving = false;


    ////////////////////
    //END OF VARIABLES//
    ////////////////////

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
        //unique to player bc controllable, other sprites will move in a pre-programmed manner

        //array of vectors for player movement, easier to read than switch
        //i.e. east = index 3({1,0}), expands to nextPosX += 1 * speed, nextPosY += 0 * speed;
        int[][] directionVals =
        {
            {0, -1},  // N
            {0, 1},   // S
            {-1, 0},  // W
            {1, 0},   // E
            {-1, -1}, // NW
            {1, -1},  // NE
            {-1, 1},  // SW
            {1, 1}    // SE
        };
        int i = this.getDirection().ordinal();

        posX += directionVals[i][0] * speed;
        posY += directionVals[i][1] * speed;

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

    public synchronized void damage(Rectangle other, double dt)
    {
        if (checkCollision(other))
        {
            //starts a thread that checks every two seconds if player is on a damaging object
            //if they are, player loses a life, stop thread if not
            //adds iFrames so player doesn't immediately die when they touch a damaging object
            if(iFrameThread == null || !iFrameThread.isAlive())
            {
                iFrameThread = new Thread( () ->
                {
                    try
                    {
                        lives -= 1;
                        Thread.sleep(2000);
                        currentFrameIndex = (currentFrameIndex + 1) % damageFrames.length;
                        image = (humanFrames[damageFrames[currentFrameIndex]]);
                    }
                    catch (InterruptedException e)
                    {
                        //player leaves damaging object hitbox
                        Thread.currentThread().interrupt();
                    }
                });
                iFrameThread.start();
            }
            bounceBack(direction, 2, dt);
        }
        else
        {
            if(iFrameThread != null)
            {
                iFrameThread.interrupt();
            }
        }
    }
    public void gainLife()
    {
        lives += 1;
    }

    public synchronized void bounceBack(Direction direction, int steps, double dt)
    {
        int[][] directionVals =
        {
                {0, 1},   // N
                {0, -1},  // S
                {1, 0},   // W
                {-1, 0},  // E
                {1, 1},   // NW
                {-1, 1},  // NE
                {1, -1},   // SW
                {-1, -1} // SE
        };
        int i = direction.ordinal();

        int targetX = getPosX() + directionVals[i][0] * 10 * steps;
        int targetY = getPosY() + directionVals[i][1] * 10 * steps;

        // Interpolate between current position and target position
        int interpolatedX = (int) (getPosX() + (targetX - getPosX()) * 1.5);
        int interpolatedY = (int) (getPosY() + (targetY - getPosY()) * 1.5);

        // Update the position
        posX = interpolatedX;
        posY = interpolatedY;


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
