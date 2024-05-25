package src.Characters;


import src.Direction;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static src.GameEngine.loadImage;

public class Enemy extends Character {
    private Image image;
    private int width;
    private int height;
    private int speed;
    private int damage;

    protected Image enemySpriteSheet = loadImage("resources/Sprites/Vampire-SpriteSheetFinal.png");
    private final Image [] enemyFrames;

    private int currentFrameIndex = 0;

    private final int[] eastFrames = {0, 1};
    private final int[] southFrames = {2, 3};
    private final int[] westFrames = {6, 7};
    private final int[] northFrames = {4, 5};
    
    public Enemy(Image image, int posX, int posY, int width, int height, int speed, int damage) {
        this.image = image;
        this.setPosX(posX);
        this.setPosY(posY);
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.damage = damage;

         int numFrames = 8,
                frameWidth = enemySpriteSheet.getWidth(null) / numFrames,
                frameHeight = enemySpriteSheet.getHeight(null);

        enemyFrames = new Image[numFrames];

        for (int i = 0; i < numFrames; i++)
        {

            enemyFrames[i] = subImage(enemySpriteSheet, i * frameWidth, 0 , frameWidth, frameHeight);
        }
        setImage(enemyFrames[0]);
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

     public void setImage(Image image) {
        this.image = image;   
    }   
    
    public Image getImage() {
        return image;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDamage() {
        return damage;
    }

    public void chasePlayer(Player player, ArrayList<Rectangle> walls)
    {
        //issue w/ enemy moving back and forth sporadically, was due to being unable to access odd coordinates
        if(posX + 1 == player.getPosX())
        {
            posX += 1;
        }
        else if(posX - 1 == player.getPosX())
        {
            posX -= 1;
        }
        if(posY + 1 == player.getPosY())
        {
            posY += 1;
        }
        else if(posY - 1 == player.getPosY())
        {
            posY -= 1;
        }

        if (posX < player.getPosX())
        {
            posX += speed;
            currentFrameIndex = (currentFrameIndex + 1) % eastFrames.length;
            image = (enemyFrames[eastFrames[currentFrameIndex]]);

        }
        else if (posX > player.getPosX())
        {
            posX -= speed;
            currentFrameIndex = (currentFrameIndex + 1) % westFrames.length;
            image = (enemyFrames[westFrames[currentFrameIndex]]);

        }

        if (posY < player.getPosY())
        {
            if(posX == player.getPosX())
            {
                currentFrameIndex = (currentFrameIndex + 1) % southFrames.length;
                image = (enemyFrames[southFrames[currentFrameIndex]]);
            }
            posY += speed;
        }
        else if (posY > player.getPosY())
        {
            if(posX == player.getPosX())
            {
                currentFrameIndex = (currentFrameIndex + 1) % northFrames.length;
                image = (enemyFrames[northFrames[currentFrameIndex]]);
            }
            posY -= speed;
        }

        handleWallCollision();

        System.out.println("Enemy new position: (" + getPosX() + ", " + getPosY() + ")");
    }


    public Rectangle getHitbox() {
        return new Rectangle(getPosX(), getPosY(), width, height);
    }
}
