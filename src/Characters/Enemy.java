package src.Characters;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Enemy extends Character
{
    private int currentFrameIndex = 0,
                damage = 0;

    private final int[] eastFrames = {0, 1},
                        southFrames = {2, 3},
                        westFrames = {6, 7},
                        northFrames = {4, 5};

    public Enemy(Image spritesheet, int posX, int posY, int speed, int damage)
    {
        super(posX, posY, speed, spritesheet, 8);
        this.damage = damage;
    };

    public void setDamage(int damage) { this.damage = damage; }
    public int getDamage() { return damage; }

    public void chasePlayer(Player player)
    {

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

        //issue w/ enemy moving back and forth sporadically, was due to being unable to access odd coordinates
        if(Math.abs(posX - player.getPosX()) <= 5)
        {
            posX = player.posX;
        }
        if(Math.abs(posY - player.getPosY()) <= 5)
        {
            posY = player.posY;
        }

        if (posX < player.getPosX())
        {
            posX += speed;
            currentFrameIndex = (currentFrameIndex + 1) % eastFrames.length;
            image = (frames[eastFrames[currentFrameIndex]]);
        }
        else if (posX > player.getPosX())
        {
            posX -= speed;
            currentFrameIndex = (currentFrameIndex + 1) % westFrames.length;
            image = (frames[westFrames[currentFrameIndex]]);
        }

        if (posY < player.getPosY())
        {
            if(posX == player.getPosX())
            {
                currentFrameIndex = (currentFrameIndex + 1) % southFrames.length;
                image = (frames[southFrames[currentFrameIndex]]);
            }
            posY += speed;
        }
        else if (posY > player.getPosY())
        {
            if(posX == player.getPosX())
            {
                currentFrameIndex = (currentFrameIndex + 1) % northFrames.length;
                image = (frames[northFrames[currentFrameIndex]]);
            }
            posY -= speed;
        }

        this.hitbox = new Rectangle(posX, posY, width, height);
        handleWallCollision();
    }
}
