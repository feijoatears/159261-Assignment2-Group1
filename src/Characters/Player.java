package src.Characters;

import src.Direction;
import src.GameEngine;
import src.Objects.Button;
import src.Objects.DamagingObject;
import src.Objects.InvisibleWall;
import src.Objects.Key;
import src.generalClasses.MazeMap;
import src.generalClasses.VolumeControl;


import javax.sound.sampled.Clip;
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



    // NATHANS SHIT ANIMATION FIX IF YOU HAVE TIME:
    private Image[] attackFrames = new Image[4];
    private int currentAttackFrame = 0;
    private boolean isAttacking = false;
    private long lastFrameTime = 0;
    private long frameDuration = 100;





    private Player()
    {
        super(0, 0, 0, loadImage("resources/Sprites/Player-SpriteSheet.png"),10);
        direction = Direction.East;
        attackFrames[0] = loadImage("resources/Sprites/SwordTestFrame1.png");
        attackFrames[1] = loadImage("resources/Sprites/SwordTestFrame2.png");
        attackFrames[2] = loadImage("resources/Sprites/SwordTestFrame3.png");
        attackFrames[3] = loadImage("resources/Sprites/SwordTestFrame4.png");
    }

    public static Player getInstance()
    {
        if(instance == null)
        {
            instance = new Player();
        }
        return instance;
    }

    public void move() {
        int nextPosX = posX,
            nextPosY = posY;

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

        nextPosX += directionVals[i][0] * speed;
        nextPosY += directionVals[i][1] * speed;


        boolean left = false,
                right = false,
                up = false,
                down = false,
                collision = false;

        for(InvisibleWall iWall : MazeMap.getInstance().getCurrentLevel().getInvisibleWalls())
        {
            Rectangle newHitbox = new Rectangle(nextPosX, nextPosY, width, height);
            if(newHitbox.intersects(iWall.getHitbox()))
            {
                if(newHitbox.x > iWall.getHitbox().x)
                {
                    left = true;
                }
                else if(newHitbox.x < iWall.getHitbox().x)
                {
                    right = true;
                }
                if(newHitbox.y > iWall.getHitbox().y)
                {
                    up = true;
                }
                else if(newHitbox.y < iWall.getHitbox().y)
                {
                    down = true;
                }
                //TODO: diagonal movement is bugged

                if(i == 0 || i == 1)
                {
                    if(up)
                    {
                        nextPosY = iWall.getPosY() + iWall.getHeight();
                    }
                    if(down)
                    {
                        nextPosY = iWall.getPosY() - height;
                    }
                }
                else
                {
                    if(left)
                    {
                        nextPosX = iWall.getPosX() + iWall.getWidth();
                    }
                    if (right)
                    {
                        nextPosX = iWall.getPosX() - width;
                    }
                }

                break;
            }
        }

        if (direction == Direction.East) {
            currentFrameIndex = (currentFrameIndex + 1) % eastFrames.length;
            image = (frames[eastFrames[currentFrameIndex]]);
        } else if (direction == Direction.West) {
            currentFrameIndex = (currentFrameIndex + 1) % westFrames.length;
            image = (frames[westFrames[currentFrameIndex]]);
        } else if (direction == Direction.North ||
                direction == Direction.Northeast ||
                direction == Direction.Northwest) {
            currentFrameIndex = (currentFrameIndex + 1) % northFrames.length;
            image = (frames[northFrames[currentFrameIndex]]);
        } else {
            currentFrameIndex = (currentFrameIndex + 1) % southFrames.length;
            image = (frames[southFrames[currentFrameIndex]]);
        }

        posX = nextPosX;
        posY = nextPosY;



        updateHitbox();





    }

    // In Player class
    public void attackUpdate(double dt) {
        if (isAttacking) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastFrameTime > frameDuration) {
                currentAttackFrame++;
                if (currentAttackFrame >= attackFrames.length) {
                    currentAttackFrame = 0;
                    isAttacking = false; // Animation finished
                }
                lastFrameTime = currentTime;
            }
        }

    }


    @Override
    public Image getImage() {
        if (isAttacking) {
            return attackFrames[currentAttackFrame];
        } else {
            return super.getImage();
        }
    }
    public boolean checkCollision(Rectangle other)
    {
        return this.hitbox.intersects(other);
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

    public synchronized void bounceBack(Direction direction, int steps) {
        int[][] directionVals = {
                {0, 1},   // N
                {0, -1},  // S
                {1, 0},   // W
                {-1, 0},  // E
                {1, 1},   // NW
                {-1, 1},  // NE
                {1, -1},  // SW
                {-1, -1}  // SE
        };
        int i = direction.ordinal();

        int targetX = getPosX() + directionVals[i][0] * 10 * steps;
        int targetY = getPosY() + directionVals[i][1] * 10 * steps;

        int interpolatedX = (int) (getPosX() + (targetX - getPosX()) * 1.5);
        int interpolatedY = (int) (getPosY() + (targetY - getPosY()) * 1.5);

        if (interpolatedX < 0) {
            interpolatedX = 0;
        } else if (interpolatedX > 450) {
            interpolatedX = 450;
        }
        if (interpolatedY < 0) {
            interpolatedY = 0;
        } else if (interpolatedY > 450) {
            interpolatedY = 450;
        }
        posX = interpolatedX;
        posY = interpolatedY;

        updateHitbox(); // Update hitbox after bouncing back

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
        Clip attackSound = VolumeControl.getInstance().getAttackSound();
        if (attackSound != null) {
            if (attackSound.isRunning()) {
                attackSound.stop();
            }
            attackSound.setFramePosition(0);
            attackSound.start();
        }
    }

    public void startAttackAnimation() {
        isAttacking = true;
        currentAttackFrame = 0;
        lastFrameTime = System.currentTimeMillis();
    }

    public boolean handleKeyCollision(Key key) {
        if (key.checkCollision(hitbox) && !key.getIsUsed()) {
            collectKey(0); // for key index 0
            key.setIsUsed(true);
            System.out.println("Key collected!");
            if (VolumeControl.getInstance().getKeyCollectedSound() != null) {
                VolumeControl.getInstance().getKeyCollectedSound().start();
            }
            // Delayed wow sound
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (VolumeControl.getInstance().getWowSound() != null) {
                        VolumeControl.getInstance().getWowSound().start();
                    }
                }
            }, 500); // Delay of 500 milliseconds
            return true;
        }
        return false;
    }

    public boolean handleButtonCollision(ArrayList<Button> buttons) {
        for (Button b : buttons) {
            if (checkCollision(b.getHitbox())) {
                if (!b.getIsUsed()) {
                    b.activate();
                    GameEngine.playAudio(b.getOnSound());
                    return true;
                }
            } else {
                if (b.getIsUsed()) {
                    b.deactivate();
                    GameEngine.playAudio(b.getOffSound());
                }
            }
        }
        return false;
    }

    public void damage(ArrayList<DamagingObject> objects, ArrayList<Enemy> enemies) {
        for (DamagingObject ob : objects) {
            if (checkCollision(ob.getHitbox())) {
                activateIFrames();
                bounceBack(direction, 2);
            }
        }
        for (Enemy e : enemies) {
            if (checkCollision(e.getHitbox())) {
                activateIFrames();
                bounceBack(direction, 2);
            }
        }
    }

    public synchronized void activateIFrames() {
        if (iFrameThread == null || !iFrameThread.isAlive()) {
            iFrameThread = new Thread(() -> {
                try {
                    lives -= 1;
                    showRedCircle = true;
                    redCircleTimer = new Timer();
                    redCircleTimer.schedule(new TimerTask() {
                        public void run() {
                            showRedCircle = false;
                            redCircleTimer.cancel();
                        }
                    }, 100);
                    if (VolumeControl.getInstance().getDamageSound() != null) {
                        VolumeControl.getInstance().getDamageSound().stop(); // Stop the clip if it's already playing
                        VolumeControl.getInstance().getDamageSound().setFramePosition(0); // Rewind to the beginning
                        VolumeControl.getInstance().getDamageSound().start(); // Play the clip
                    }
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            iFrameThread.start();
        }
    }

    public void showDamagedCircle(Graphics2D g) {
        if (showRedCircle) {
            g.setColor(new Color(255, 0, 0, 128));
            g.fillOval(posX, posY, width, height);
        }
    }

    public void reset() {
        // Reset player's state
        setLives(3);
        setPosX(100);
        setPosY(250);
        setSpeed(20);
        setMoving(false);
        // Reset other player-specific variables if needed
    }
}
