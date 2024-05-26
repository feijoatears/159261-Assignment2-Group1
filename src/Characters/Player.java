package src.Characters;

import src.Direction;
import src.GameEngine;
import src.Objects.Button;
import src.Objects.DamagingObject;
import src.Objects.Key;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static src.GameEngine.*;



public class Player extends Character
{
    private static Player instance;

    private static Timer redCircleTimer;
    private boolean showRedCircle = false;
    
    private final Image heartImage = loadImage("resources/Objects/heart.png");

    private final int[] eastFrames = {0, 1},
                        southFrames = {2, 3},
                       damageFrames = {4, 5},
                        westFrames = {8, 9},
                        northFrames = {6, 7};

    private int lives = 3,
                currentFrameIndex = 0;

    private static Thread iFrameThread;

    public boolean[] hasKey = new boolean[10];
    private boolean isMoving = false;

    private Player()
    {
        super(0, 0, 0, loadImage("resources/Sprites/Player-SpriteSheet.png"),10);
        direction = Direction.East;
    }

    public static Player getInstance()
    {
        if(instance == null)
        {
            instance = new Player();
        }
        return instance;
    }

    public void move()
    {
        hitbox = new Rectangle(getPosX(), getPosY(), getImage().getWidth(null), getImage().getHeight(null));

        //array of vectors for player movement, easier to read than switch
        //i.e. east = index 3({1,0}), evaluates to nextPosX += 1 * speed, nextPosY += 0 * speed;
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
            image = (frames[eastFrames[currentFrameIndex]]);
        }
        else if (direction == Direction.West)
        {
            currentFrameIndex = (currentFrameIndex + 1) % westFrames.length;
            image = (frames[westFrames[currentFrameIndex]]);
        }
        else if (direction == Direction.North ||
                direction == Direction.Northeast ||
                direction == Direction.Northwest)
        {
            currentFrameIndex = (currentFrameIndex + 1) % northFrames.length;
            image = (frames[northFrames[currentFrameIndex]]);
        }
        else
        {
            currentFrameIndex = (currentFrameIndex + 1) % southFrames.length;
            image = (frames[southFrames[currentFrameIndex]]);
        }
    }

    public boolean checkCollision(Rectangle other)
    {
        Rectangle playerRect = new Rectangle(posX, posY, width, height);
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
    public void setLives(int lives)
    {
        this.lives = lives;
    }
    public int getLives()
    {
        return lives;
    }
    public void gainLife()
    {
        lives += 1;
    }

    public synchronized void bounceBack(Direction direction, int steps)
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

        int interpolatedX = (int) (getPosX() + (targetX - getPosX()) * 1.5);
        int interpolatedY = (int) (getPosY() + (targetY - getPosY()) * 1.5);

        if(interpolatedX < 0)
        {
            interpolatedX = 0;
        }
        else if(interpolatedX > 450)
        {
            interpolatedX = 450;
        }
        if(interpolatedY < 0)
        {
            interpolatedY = 0;
        }
        else if (interpolatedY > 450)
        {
            interpolatedY = 450;
        }
        posX = interpolatedX;
        posY = interpolatedY;
    }

    public void attack(ArrayList<Enemy> enemies) {
        Rectangle attackRange = new Rectangle(getPosX() - 10, getPosY() - 10, width + 20, height + 20);
        for (Enemy enemy : enemies) {
            if (attackRange.intersects(enemy.getHitbox())) {
                // Damage the enemy or remove it
                // Example: Remove enemy for simplicity
                enemies.remove(enemy);
                break;
            }
        }
    }

    public boolean handleKeyCollision(Key key)
    {
        if (key.checkCollision(hitbox) && !key.getIsUsed())
        {
            collectKey(0); // for keyindex 0
            key.setUsed(true);
            System.out.println("Key collected!");
            return true;
        }
        return false;
    }
    public boolean handleButtonCollision(ArrayList<Button> buttons)
    {
        for (Button b : buttons)
        {
            if (checkCollision(b.getHitbox()))
            {
                if (!b.getIsUsed())
                {
                    b.activate();
                    playAudio(b.getOnSound());
                    return true;
                }
            }
            else
            {
                if (b.getIsUsed())
                {
                    b.deactivate();
                    playAudio(b.getOffSound());
                }
            }
        }
        return false;
    }

    public void damage(ArrayList<DamagingObject> objects, ArrayList<Enemy> enemies)
    {
        for(DamagingObject ob : objects)
        {
            if (checkCollision(ob.getHitbox()))
            {
                //starts a thread that checks every two seconds if player is on a damaging object
                //if they are, player loses a life, stop thread if not
                //adds iFrames so player doesn't immediately die when they touch a damaging object

                activateIFrames();
                bounceBack(direction, 2);
            }
        }
        for (Enemy e : enemies)
        {
            if(checkCollision(e.getHitbox()))
            {
                activateIFrames();
                bounceBack(direction, 2);
            }
        }
    }
    public synchronized void activateIFrames()
    {
        if(iFrameThread == null || !iFrameThread.isAlive())
        {
            iFrameThread = new Thread( () ->
            {
                try
                {
                    lives -= 1;
                    showRedCircle = true;
                    redCircleTimer = new Timer();
                    redCircleTimer.schedule(new TimerTask()
                    {
                        public void run ()
                        {
                            showRedCircle = false;
                            redCircleTimer.cancel();
                        }
                    }, 100);
                    Thread.sleep(2000);
                }
                catch (InterruptedException e)
                {
                    //player leaves damaging object hitbox
                    Thread.currentThread().interrupt();
                }
            });
            iFrameThread.start();
        }
    }
    public void showDamagedCircle(Graphics2D g)
    {
        if (showRedCircle)
        {
            g.setColor(new Color(255,0,0,128));
            g.fillOval(posX, posY, width, height);
        }
    }
}
