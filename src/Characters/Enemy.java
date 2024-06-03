package src.Characters;
import src.Objects.*;
import src.generalClasses.*;
import java.awt.*;


public abstract class Enemy extends Character
{
    private int currentFrameIndex = 0;

    private final int[] eastFrames = {0, 1},
                        southFrames = {2, 3},
                        westFrames = {6, 7},
                        northFrames = {4, 5};

    public Enemy(int posX, int posY, int speed, String frameString)
    {
        super(posX, posY, speed, frameString, 8);
    }

    public void chasePlayer(Player player, Level level) {
        int targetX = player.getPosX();
        int targetY = player.getPosY();

        // Define the hit range
        int hitRange = 0;

        if (Math.abs(posX - targetX) <= hitRange && Math.abs(posY - targetY) <= hitRange) {
            // The enemy is close enough to hit the player
            // Perform hit logic
            System.out.println("Enemy hits the player!");
            return;
        }

        // Issue w/ enemy moving back and forth sporadically, was due to being unable to access odd coordinates
        if (Math.abs(posX - player.getPosX()) <= 5) {
            posX = player.posX;
        }
        if (Math.abs(posY - player.getPosY()) <= 5) {
            posY = player.posY;
        }

        if (posX < player.getPosX()) {
            posX += speed;
            currentFrameIndex = (currentFrameIndex + 1) % eastFrames.length;
            image = frames[eastFrames[currentFrameIndex]];
        } else if (posX > player.getPosX()) {
            posX -= speed;
            currentFrameIndex = (currentFrameIndex + 1) % westFrames.length;
            image = frames[westFrames[currentFrameIndex]];
        }

        if (posY < player.getPosY()) {
            if (posX == player.getPosX()) {
                currentFrameIndex = (currentFrameIndex + 1) % southFrames.length;
                image = frames[southFrames[currentFrameIndex]];
            }
            posY += speed;
        } else if (posY > player.getPosY()) {
            if (posX == player.getPosX()) {
                currentFrameIndex = (currentFrameIndex + 1) % northFrames.length;
                image = frames[northFrames[currentFrameIndex]];
            }
            posY -= speed;
        }

        this.hitbox = new Rectangle(posX, posY, width, height);
        handleWallCollision(level);
    }
    public void handleIWallCollision(Level l)
    {
        for(InvisibleWall iWall : l.getInvisibleWalls())
        {
            if(hitbox.intersects(iWall.getHitbox()))
            {
                if(Math.abs(posX - (iWall.getHitbox().x + iWall.getHitbox().width)) <= 10)
                {
                    posX = (iWall.getHitbox().x + iWall.getHitbox().width);
                }
                if(Math.abs(posX + width - (iWall.getHitbox().x)) <= 10)
                {
                    posX = (iWall.getHitbox().x - width);
                }
                if(Math.abs(posY - (iWall.getHitbox().y + iWall.getHitbox().height)) <= 10)
                {
                    posY = (iWall.getHitbox().y + iWall.getHitbox().height);
                }
                if(Math.abs(posY + height - (iWall.getHitbox().y)) <= 10)
                {
                    posY = (iWall.getHitbox().y - height);
                }
                break;
            }
        }
    }
}
