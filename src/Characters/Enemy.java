package src.Characters;

import src.Direction;
import java.awt.*;
import java.util.ArrayList;

public class Enemy extends Character {
    private Image image;
    private int width;
    private int height;
    private int speed;
    private int damage;

    public Enemy(Image image, int posX, int posY, int width, int height, int speed, int damage) {
        this.image = image;
        this.setPosX(posX);
        this.setPosY(posY);
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.damage = damage;
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

    public void chasePlayer(Player player, ArrayList<Rectangle> walls) {
        int playerX = player.getPosX();
        int playerY = player.getPosY();

        int newPosX = getPosX();
        int newPosY = getPosY();

        if (newPosX < playerX) {
            newPosX += speed;
        } else if (newPosX > playerX) {
            newPosX -= speed;
        }

        if (newPosY < playerY) {
            newPosY += speed;
        } else if (newPosY > playerY) {
            newPosY -= speed;
        }

        // Check for collisions with walls at new positions
        if (!checkWallCollision(walls, newPosX, getPosY())) {
            setPosX(newPosX);
        }

        if (!checkWallCollision(walls, getPosX(), newPosY)) {
            setPosY(newPosY);
        }

        System.out.println("Enemy new position: (" + getPosX() + ", " + getPosY() + ")");
    }

    private boolean checkWallCollision(ArrayList<Rectangle> walls, int x, int y) {
        Rectangle enemyRect = new Rectangle(x, y, width, height);
        for (Rectangle wall : walls) {
            if (enemyRect.intersects(wall)) {
                System.out.println("Collision with wall at: (" + wall.x + ", " + wall.y + ")");
                return true;
            }
        }
        return false;
    }

    public Rectangle getHitbox() {
        return new Rectangle(getPosX(), getPosY(), width, height);
    }
}
